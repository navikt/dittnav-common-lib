package no.nav.personbruker.dittnav.common.util.kafka.producer

import no.nav.personbruker.dittnav.common.util.kafka.RecordKeyValueWrapper
import no.nav.personbruker.dittnav.common.util.kafka.exception.RetriableKafkaException
import no.nav.personbruker.dittnav.common.util.kafka.exception.UnretriableKafkaException
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import org.slf4j.LoggerFactory

class KafkaProducerWrapper<K, V>(
    private val topicName: String,
    private val kafkaProducer: KafkaProducer<K, V>
) {

    val log = LoggerFactory.getLogger(KafkaProducerWrapper::class.java)

    fun sendEventsTransactionally(events: List<RecordKeyValueWrapper<K, V>>) {
        try {
            kafkaProducer.beginTransaction()
            events.forEach { event ->
                sendEvent(event)
            }
            kafkaProducer.commitTransaction()
        } catch (e: KafkaException) {
            kafkaProducer.abortTransaction()
            throw RetriableKafkaException("Et eller flere eventer feilet med en periodisk feil ved sending til kafka", e)
        } catch (e: Exception) {
            kafkaProducer.close()
            throw UnretriableKafkaException("Fant en uventet feil ved sending av eventer til kafka", e)
        }
    }

    fun sendEvent(key: K, event: V) {
        ProducerRecord(topicName, key, event).let { producerRecord ->
            kafkaProducer.send(producerRecord) { _, exception ->
                when (exception.javaClass) {
                    KafkaException::class.java -> throw RetriableKafkaException("Sending av event feilet med en periodisk feil mot kafka", exception)
                    else -> throw UnretriableKafkaException("Fant en uventet feil ved sending av event til kafka", exception)
                }
            }
        }
    }

    fun flushAndClose() {
        try {
            kafkaProducer.flush()
            kafkaProducer.close()
            log.info("Produsent for kafka-eventer er flushet og lukket.")
        } catch (e: Exception) {
            log.warn("Klarte ikke å flushe og lukke produsent. Det kan være eventer som ikke ble produsert.")
        }
    }

    private fun sendEvent(event: RecordKeyValueWrapper<K, V>) {
        val producerRecord = ProducerRecord(topicName, event.key, event.value)
        kafkaProducer.send(producerRecord)
    }
}