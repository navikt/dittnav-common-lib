package no.nav.personbruker.dittnav.common.metrics.influxdb

data class InfluxConfig (
    val hostName: String,
    val userName: String,
    val password: String,
    val hostPort: Int,
    val databaseName: String,
    val retentionPolicyName: String,
    val applicationName: String,
    val clusterName: String,
    val namespace: String,
    val enableEventBatching: Boolean = true,
)
