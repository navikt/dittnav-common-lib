package no.nav.personbruker.dittnav.common.metrics.influxdb

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

internal object UnitsSinceEpochHelper {
    fun unitsSinceEpoch(unit: TimeUnit, toDate: Instant = Instant.now()): Long {
        return when(unit) {
            TimeUnit.SECONDS -> ChronoUnit.SECONDS.between(Instant.EPOCH, toDate)
            TimeUnit.MILLISECONDS -> ChronoUnit.MILLIS.between(Instant.EPOCH, toDate)
            TimeUnit.MICROSECONDS -> ChronoUnit.MICROS.between(Instant.EPOCH, toDate)
            TimeUnit.NANOSECONDS -> ChronoUnit.NANOS.between(Instant.EPOCH, toDate)
            else -> throw IllegalStateException("Invalid time precision")
        }
    }
}
