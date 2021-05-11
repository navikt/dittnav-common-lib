package no.nav.personbruker.dittnav.common.metrics.influx

data class InfluxConfig (
    val hostName: String,
    val userName: String,
    val password: String,
    val hostPort: Int,
    val applicationName: String,
    val clusterName: String,
    val namespace: String,
    val enableEventBatching: Boolean = true,
)
