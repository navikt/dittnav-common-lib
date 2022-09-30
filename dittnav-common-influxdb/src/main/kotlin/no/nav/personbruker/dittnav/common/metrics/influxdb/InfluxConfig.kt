package no.nav.personbruker.dittnav.common.metrics.influxdb

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

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
    val timePrecision: TimeUnit = MILLISECONDS
) {
    init {
        require( timePrecision in listOf( SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS ) ) {
            "timePrecision må være en av [SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS]."
        }
    }
}
