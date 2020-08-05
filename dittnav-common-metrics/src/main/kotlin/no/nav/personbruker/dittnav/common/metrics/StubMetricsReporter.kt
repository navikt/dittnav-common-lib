package no.nav.personbruker.dittnav.common.metrics

import org.slf4j.LoggerFactory


class StubMetricsReporter : MetricsReporter {

    val log = LoggerFactory.getLogger(StubMetricsReporter::class.java)

    override suspend fun registerDataPoint(measurement: String, fields: Map<String, Any>, tags: Map<String, String>) {
        log.debug("Data point: { measurement: $measurement, fields: $fields, tags: $tags }")
    }
}