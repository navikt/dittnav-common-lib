package no.nav.personbruker.dittnav.common.metrics.influx

import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

class InfluxMetricsReporter internal constructor(influxConfig: InfluxConfig, private val dataPointRelay: DataPointRelay) : MetricsReporter {

    constructor(influxConfig: InfluxConfig) : this(influxConfig, DataPointRelayFactory.createDataPointRelay(influxConfig))

    override suspend fun registerDataPoint(measurementName: String, fields: Map<String, Any>, tags: Map<String, String>) {
        val point = Point.measurement(measurementName)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag(tags)
                .tag(DEFAULT_TAGS)
                .fields(fields)
                .build()

        dataPointRelay.submitDataPoint(point)
    }

    private val DEFAULT_TAGS = listOf(
        "application" to influxConfig.applicationName,
        "cluster" to influxConfig.clusterName,
        "namespace" to influxConfig.namespace
    ).toMap()
}
