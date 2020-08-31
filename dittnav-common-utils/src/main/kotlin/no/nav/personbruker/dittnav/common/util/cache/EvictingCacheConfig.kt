package no.nav.personbruker.innloggingsstatus.pdl.cache

data class EvictingCacheConfig (
    val evictionThreshold: Int = 1024,
    val entryLifetimeMinutes: Long = 15,
    val massEvictionCoolDownMinutes: Long = 5
)