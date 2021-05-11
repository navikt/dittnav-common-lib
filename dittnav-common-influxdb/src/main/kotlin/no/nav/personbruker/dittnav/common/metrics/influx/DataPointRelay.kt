package no.nav.personbruker.dittnav.common.metrics.influx

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import org.slf4j.LoggerFactory

internal class DataPointRelay(private val influxDB: InfluxDB) {

    private val log = LoggerFactory.getLogger(DataPointRelay::class.java)

    suspend fun submitDataPoint(point: Point) = withContext(Dispatchers.IO) {
        try {
            influxDB.write(point)
        } catch (e: Exception) {
            log.warn("Klarte ikke skrive event til InfluxDB.", e)
        }
    }
}

internal object DataPointRelayFactory {
    internal fun createDataPointRelay(influxConfig: InfluxConfig): DataPointRelay {
        val influxDb = InfluxDBFactory.connect(
            "https://${influxConfig.hostName}:${influxConfig.hostPort}",
            influxConfig.userName,
            influxConfig.password
        )

        influxDb.setDatabase(influxConfig.databaseName)

        if (influxConfig.enableEventBatching) {
            influxDb.enableBatch()
        } else {
            influxDb.disableBatch()
        }

        return DataPointRelay(influxDb)
    }
}
