package no.nav.personbruker.dittnav.common.metrics.influx

data class SensuConfig (
    val hostName: String,
    val hostPort: Int,
    val eventsTopLevelName: String,
    val applicationName: String,
    val clusterName: String,
    val namespace: String
)