package no.nav.personbruker.dittnav.common.metrics.influx

data class SensuConfig (
    val hostName: String,
    val hostPort: Int,
    val eventsTopLevelName: String,
    val applicationName: String,
    val clusterName: String,
    val namespace: String,
    val enableEventBatching: Boolean = false,
    val eventBatchesPerSecond: Int = 3
) {
    init {
        require(eventBatchesPerSecond in 1..100) { "Batcher sendt per sekund må være mellom inklusive 1 og 100, men var $eventBatchesPerSecond." }
    }
}