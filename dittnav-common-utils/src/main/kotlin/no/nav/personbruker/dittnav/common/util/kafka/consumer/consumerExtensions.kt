package no.nav.personbruker.dittnav.common.util.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer

fun <K, V> KafkaConsumer<K, V>.rollbackToLastCommitted() {
    committed(assignment()).forEach { (partition, metadata) ->
        seek(partition, metadata.offset())
    }
}

fun <K, V> KafkaConsumer<K, V>.resetGroupIdOffsetsToZero() {
    assignment().forEach { partition ->
        seek(partition, 0)
    }
}

fun <K, V> ConsumerRecords<K, V>.hasAnyRecords(): Boolean {
    return !isEmpty
}
