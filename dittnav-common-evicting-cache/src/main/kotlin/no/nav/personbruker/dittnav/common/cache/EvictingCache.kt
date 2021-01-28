package no.nav.personbruker.dittnav.common.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit

class EvictingCache<K, V>(config: EvictingCacheConfig = EvictingCacheConfig()) {

    private val evictionThreshold = config.evictionThreshold
    private val massEvictionCoolDownMinutes = config.massEvictionCoolDownMinutes

    private val entryMap = mutableMapOf<K, EntryWrapper<K, V>>()
    private val entryOrder = mutableListOf<EntryWrapper<K, V>>()

    private val mutex = Mutex()

    private var lastEviction: Instant? = null

    private val wrapperBuilder = { key: K, value: V -> EntryWrapper(key, value, config.entryLifetimeMinutes) }


    suspend fun getEntry(key: K, supplier: suspend (K) -> V?): V? {

        val entry = readEntry(key)

        return when {
            entry == null -> addEntry(key, supplier)
            entry.isExpired() -> replaceEntry(key, supplier)
            else -> entry.value
        }
    }

    private suspend fun readEntry(key: K) = mutex.withLock {
        entryMap[key]
    }

    private suspend fun addEntry(key: K, supplier: suspend (K) -> V?): V? {
        checkThreshold()

        val entry = supplier(key)

        if (entry == null) {
            return null
        }
        val entryWrapper = wrapperBuilder(key, entry)

        mutex.withLock {
            entryOrder.add(entryWrapper)
            entryMap[key] = entryWrapper
        }

        return entry
    }

    private suspend fun checkThreshold() {
        if (entryMap.size >= evictionThreshold && canPerformMassEviction()) {
            evictAllExpired()
        }
    }

    private suspend fun canPerformMassEviction(): Boolean = mutex.withLock {
        return lastEviction?.plus(massEvictionCoolDownMinutes, ChronoUnit.MINUTES)
            ?.isBefore(Instant.now())
            ?: true
    }

    private suspend fun replaceEntry(key: K, supplier: suspend (K) -> V?): V? {
        evictEntry(key)
        return addEntry(key, supplier)
    }


    private suspend fun evictEntry(key: K) = mutex.withLock {
        entryMap.remove(key)?.let {
            entryOrder.remove(it)
        }
    }

    private suspend fun evictAllExpired() {
        val start = System.nanoTime()
        val evicted = performEvictionOfExpiredEntries()
        val durationNanos = System.nanoTime() - start
        log.debug("Evicted $evicted entries from cache in ${durationNanos / 1000} µs.")
    }

    private suspend fun performEvictionOfExpiredEntries(): Int = mutex.withLock {
        val numberOfEntriesToEvict = 1 + entryOrder.indexOfLast { it.isExpired() }
        val remainingValidEntries = entryOrder.partitionFromIndex(numberOfEntriesToEvict)

        entryMap.clear()
        remainingValidEntries.map {
            it.key to it
        }.toMap(entryMap)

        entryOrder.clear()
        entryOrder.addAll(remainingValidEntries)

        lastEviction = Instant.now()

        return numberOfEntriesToEvict
    }

    companion object {
        private val log = LoggerFactory.getLogger(EvictingCache::class.java)
    }
}

private data class EntryWrapper<K, V> (
    val key: K,
    val value: V,
    val entryLifeTimeMinutes: Long
) {
    val timeOfCreation: Instant = Instant.now()
    val timeOfExpiry: Instant = timeOfCreation.plus(entryLifeTimeMinutes, ChronoUnit.MINUTES)

    fun isExpired() = timeOfExpiry < Instant.now()
}

fun <T> List<T>.partitionFromIndex(indexInclusive: Int): List<T> {
    return when {
        indexInclusive < 1 -> toList()
        indexInclusive >= size -> emptyList()
        else -> subList(indexInclusive, size).toList()
    }
}
