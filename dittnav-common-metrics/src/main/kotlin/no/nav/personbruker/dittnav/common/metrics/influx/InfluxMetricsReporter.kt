package no.nav.personbruker.dittnav.common.metrics.influx

import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

class InfluxMetricsReporter internal constructor(sensuConfig: SensuConfig, private val dataPointRelay: DataPointRelay) : MetricsReporter {

    constructor(sensuConfig: SensuConfig) : this(sensuConfig, DataPointRelayFactory.createDataPointRelay(sensuConfig))

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
        "application" to sensuConfig.applicationName,
        "cluster" to sensuConfig.clusterName,
        "namespace" to sensuConfig.namespace
    ).toMap()
}