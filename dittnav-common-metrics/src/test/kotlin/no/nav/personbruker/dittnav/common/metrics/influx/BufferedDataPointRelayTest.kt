package no.nav.personbruker.dittnav.common.metrics.influx

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.common.metrics.util.delayUntilTrue
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test

internal class BufferedDataPointRelayTest {
    private val topLevelName = "testApp"

    private val sensuClient: SensuClient = mockk()

    private val dataPointRelay = BufferedDataPointRelay(sensuClient, topLevelName, 50)

    @Test
    fun `Should buffer events and send in bulk`() {
        val dataPoints = InfluxDataPointObjectMother.createSimplePointsForMeasurents("measurement", 10)

        var pointsSubmitted = 0
        val eventSlot = slot<SensuEvent>()
        coEvery { sensuClient.submitEvent(capture(eventSlot)) } answers {
            pointsSubmitted += eventSlot.captured.dataPoints.size
            Unit
        }

        runBlocking {
            dataPoints.forEach {
                dataPointRelay.submitDataPoint(it)
            }

            delayUntilTrue {
                pointsSubmitted == dataPoints.size
            }
        }

        // The 'atMost' value here is a bit arbitrary, but it should suffice. What we want to achieve here
        // is to prove that some batching of data points occurred, while acknowledging potential timing issues
        coVerify (  atMost = dataPoints.size / 2 ) { sensuClient.submitEvent(any()) }
        pointsSubmitted `should equal` dataPoints.size
    }

    @Test
    fun `Should add metrics context name to event`() {
        val dataPoint = InfluxDataPointObjectMother.createSimplePointsForMeasurents("measurement", 1)[0]

        var pointsSubmitted = 0
        val eventSlot = slot<SensuEvent>()
        coEvery { sensuClient.submitEvent(capture(eventSlot)) } answers {
            pointsSubmitted += eventSlot.captured.dataPoints.size
            Unit
        }

        runBlocking {
            dataPointRelay.submitDataPoint(dataPoint)

            delayUntilTrue {
                pointsSubmitted == 1
            }
        }

        eventSlot.captured.name `should equal` topLevelName
    }
}