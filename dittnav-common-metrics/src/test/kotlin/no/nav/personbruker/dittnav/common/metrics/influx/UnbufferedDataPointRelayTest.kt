package no.nav.personbruker.dittnav.common.metrics.influx

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.common.metrics.influx.InfluxDataPointObjectMother.createSimplePointsForMeasurents
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test

internal class UnbufferedDataPointRelayTest {

    private val topLevelName = "testApp"

    private val sensuClient: SensuClient = mockk()

    private val dataPointRelay = UnbufferedDataPointRelay(sensuClient, topLevelName)

    @Test
    fun `Should send an event for each data point`() {
        val dataPoints = createSimplePointsForMeasurents("measurement", 5)

        coEvery { sensuClient.submitEvent(any()) } returns Unit

        runBlocking {
            dataPoints.forEach {
                dataPointRelay.submitDataPoint(it)
            }
        }

        coVerify (exactly = dataPoints.size) { sensuClient.submitEvent(any()) }
     }

    @Test
    fun `Should add metrics context name to event`() {
        val dataPoint = createSimplePointsForMeasurents("measurement", 1)[0]

        val eventSlot = slot<SensuEvent>()
        coEvery { sensuClient.submitEvent(capture(eventSlot)) } returns Unit

        runBlocking {
            dataPointRelay.submitDataPoint(dataPoint)
        }

        eventSlot.captured.name `should equal` topLevelName
     }


}