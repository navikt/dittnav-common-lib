package no.nav.personbruker.dittnav.common.cache

data class EvictingCacheConfig (
    val evictionThreshold: Int = 1024,
    val entryLifetimeMinutes: Long = 15,
    val massEvictionCoolDownMinutes: Long = 5
)
