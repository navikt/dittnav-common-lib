package no.nav.personbruker.dittnav.common.util.kafka

data class RecordKeyValueWrapper <K, V> (
    val key: K,
    val value: V
)