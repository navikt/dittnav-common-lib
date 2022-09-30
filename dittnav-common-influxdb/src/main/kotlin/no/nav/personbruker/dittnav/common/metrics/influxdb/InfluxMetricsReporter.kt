package no.nav.personbruker.dittnav.common.metrics.influxdb

import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import org.influxdb.dto.Point

class InfluxMetricsReporter internal constructor(influxConfig: InfluxConfig, private val dataPointRelay: DataPointRelay) : MetricsReporter {

    private val timePrecision = influxConfig.timePrecision

    constructor(influxConfig: InfluxConfig) : this(influxConfig, DataPointRelayFactory.createDataPointRelay(influxConfig))

    override suspend fun registerDataPoint(measurementName: String, fields: Map<String, Any>, tags: Map<String, String>) {
        val point = Point.measurement(measurementName)
                .setTime()
                .tag(tags)
                .tag(DEFAULT_TAGS)
                .fields(fields)
                .build()

        dataPointRelay.submitDataPoint(point)
    }

    private fun Point.Builder.setTime() = time(UnitsSinceEpochHelper.unitsSinceEpoch(timePrecision), timePrecision)

    private val DEFAULT_TAGS = listOf(
        "application" to influxConfig.applicationName,
        "cluster" to influxConfig.clusterName,
        "namespace" to influxConfig.namespace
    ).toMap()
}
