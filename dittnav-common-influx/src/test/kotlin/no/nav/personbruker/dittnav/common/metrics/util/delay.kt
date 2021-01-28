package no.nav.personbruker.dittnav.common.metrics.util

import kotlinx.coroutines.delay

suspend fun delayUntilTrue(maxDelayMs: Long = 5000, predicateIntervalMs: Long = 10, predicate: () -> Boolean) {
    val start = System.currentTimeMillis()
    while (!predicate()) {
        delay(predicateIntervalMs)

        if (System.currentTimeMillis() - start > maxDelayMs) {
            throw IllegalStateException("Condition was not reached before ${maxDelayMs}ms")
        }
    }
}