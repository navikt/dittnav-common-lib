package no.nav.personbruker.dittnav.common.metrics.influx

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.influxdb.dto.Point
import kotlin.concurrent.fixedRateTimer

internal interface DataPointRelay {
    suspend fun submitDataPoint(point: Point)
}

internal object DataPointRelayFactory {
    internal fun createDataPointRelay(sensuConfig: SensuConfig): DataPointRelay {
        return if (sensuConfig.enableEvenBatching) {
            BufferedDataPointRelay(sensuConfig)
        } else {
            UnbufferedDataPointRelay(sensuConfig)
        }
    }
}

private class UnbufferedDataPointRelay(sensuConfig: SensuConfig): DataPointRelay {

    private val sensuClient = SensuClient(sensuConfig.hostName, sensuConfig.hostPort)
    private val eventsTopLevelName = sensuConfig.eventsTopLevelName

    override suspend fun submitDataPoint(point: Point) {
        sensuClient.submitEvent(SensuEvent(listOf(point), eventsTopLevelName))
    }
}

private class BufferedDataPointRelay(sensuConfig: SensuConfig): DataPointRelay {

    private val sensuClient = SensuClient(sensuConfig.hostName, sensuConfig.hostPort)
    private val eventsTopLevelName = sensuConfig.eventsTopLevelName

    private var dataPointBuffer = mutableListOf<Point>()
    private val mutex = Mutex()

    private val batchIntervalMs = 1000L / sensuConfig.eventBatchesPerSecond


    override suspend fun submitDataPoint(point: Point) = mutex.withLock<Unit> {
        dataPointBuffer.add(point)
    }

    init {
        fixedRateTimer(daemon = true, initialDelay = batchIntervalMs, period = batchIntervalMs) {
            runBlocking(Dispatchers.IO) {
                flushDataPoints()
            }
        }
    }

    private suspend fun flushDataPoints() {

        var currentPointBuffer: List<Point> = emptyList()

        mutex.withLock {
            if (dataPointBuffer.isNotEmpty()) {
                currentPointBuffer = dataPointBuffer
                dataPointBuffer = mutableListOf()
            }
        }

        if (currentPointBuffer.isNotEmpty()) {
            sensuClient.submitEvent(SensuEvent(currentPointBuffer, eventsTopLevelName))
        }
    }
}