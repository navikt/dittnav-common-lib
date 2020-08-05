package no.nav.personbruker.dittnav.common.metrics

interface MetricsReporter {
    suspend fun registerDataPoint(measurement: String, fields: Map<String, Any>, tags: Map<String, String>)
}