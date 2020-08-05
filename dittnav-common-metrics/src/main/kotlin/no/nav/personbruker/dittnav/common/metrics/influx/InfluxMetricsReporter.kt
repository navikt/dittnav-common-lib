package no.nav.personbruker.dittnav.common.metrics.influx

import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

class InfluxMetricsReporter(sensuConfig: SensuConfig) : MetricsReporter {

    private val sensuClient = SensuClient(sensuConfig.hostName, sensuConfig.hostPort)
    private val eventsTopLevelName = sensuConfig.eventsTopLevelName

    override suspend fun registerDataPoint(measurement: String, fields: Map<String, Any>, tags: Map<String, String>) {
        val point = Point.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag(tags)
                .tag(DEFAULT_TAGS)
                .fields(fields)
                .build()

        sensuClient.submitEvent(SensuEvent(point, eventsTopLevelName))
    }

    private val DEFAULT_TAGS = listOf(
        "application" to sensuConfig.applicationName,
        "cluster" to sensuConfig.clusterName,
        "namespace" to sensuConfig.namespace
    ).toMap()
}