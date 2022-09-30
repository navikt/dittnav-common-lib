package no.nav.personbruker.dittnav.common.metrics.influxdb

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit.*

internal class UnitsSinceEpochHelperTest {
    private val epochSeconds = 100200300L
    private val nanos = 400500600L

    private val endDate = Instant.ofEpochSecond(epochSeconds, nanos)

    @Test
    fun `Finner riktig antall sekunder siden epoch`() {
        val secondsSinceEpoch = UnitsSinceEpochHelper.unitsSinceEpoch(SECONDS, endDate)

        secondsSinceEpoch shouldBe 100200300L
    }

    @Test
    fun `Finner riktig antall millisekunder siden epoch`() {
        val millisecondsSinceEpoch = UnitsSinceEpochHelper.unitsSinceEpoch(MILLISECONDS, endDate)

        millisecondsSinceEpoch shouldBe 100200300400L
    }

    @Test
    fun `Finner riktig antall mikrosekunder siden epoch`() {
        val microsecondsSinceEpoch = UnitsSinceEpochHelper.unitsSinceEpoch(MICROSECONDS, endDate)

        microsecondsSinceEpoch shouldBe 100200300400500L
    }

    @Test
    fun `Finner riktig antall nanosekunder siden epoch`() {
        val nanosecondsSinceEpoch = UnitsSinceEpochHelper.unitsSinceEpoch(NANOSECONDS, endDate)

        nanosecondsSinceEpoch shouldBe 100200300400500600L
    }

}
