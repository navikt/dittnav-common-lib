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
        return if (sensuConfig.enableEventBatching) {
            BufferedDataPointRelay(sensuConfig)
        } else {
            UnbufferedDataPointRelay(sensuConfig)
        }
    }
}

internal class UnbufferedDataPointRelay (
    private val sensuClient: SensuClient,
    private val eventsTopLevelName: String
): DataPointRelay {

    constructor(sensuConfig: SensuConfig) :
            this(SensuClient(sensuConfig.hostName, sensuConfig.hostPort), sensuConfig.eventsTopLevelName)


    override suspend fun submitDataPoint(point: Point) {
        sensuClient.submitEvent(SensuEvent(listOf(point), eventsTopLevelName))
    }
}

internal class BufferedDataPointRelay (
    private val sensuClient: SensuClient,
    private val eventsTopLevelName: String,
    batchIntervalMs: Long
): DataPointRelay {

    constructor(sensuConfig: SensuConfig) : this(
        SensuClient(sensuConfig.hostName, sensuConfig.hostPort),
        sensuConfig.eventsTopLevelName,
        1000L / sensuConfig.eventBatchesPerSecond
    )

    private var dataPointBuffer = mutableListOf<Point>()
    private val mutex = Mutex()

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