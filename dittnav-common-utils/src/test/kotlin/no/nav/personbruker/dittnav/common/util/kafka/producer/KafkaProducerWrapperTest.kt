package no.nav.personbruker.dittnav.common.util.kafka.producer

import io.mockk.*
import no.nav.personbruker.dittnav.common.util.kafka.RecordKeyValueWrapper
import no.nav.personbruker.dittnav.common.util.kafka.exception.RetriableKafkaException
import no.nav.personbruker.dittnav.common.util.kafka.exception.UnretriableKafkaException
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.KafkaException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class KafkaProducerWrapperTest {

    private val topicName = "topicName"
    private val producer: KafkaProducer<Int, String> = mockk()

    private val events = listOf(
        RecordKeyValueWrapper(1, "one"),
        RecordKeyValueWrapper(2, "two"),
        RecordKeyValueWrapper(3, "three")
    )

    private val producerWrapper = KafkaProducerWrapper(topicName, producer)

    @AfterEach
    fun cleanUp() {
        clearMocks(producer)
    }

    @Test
    fun `Should correctly send events as part of a transaction`() {
        every { producer.beginTransaction() } returns Unit
        every { producer.commitTransaction() } returns Unit
        every { producer.send(any()) } returns null

        producerWrapper.sendEventsTransactionally(events)

        verifyOrder {
            producer.beginTransaction()
            producer.send(any())
            producer.commitTransaction()
        }

        verify(exactly = events.size) { producer.send(any()) }
    }

    @Test
    fun `Encountering a KafkaException during a transaction should abort current transaction and throw a RetriableKafkaException`() {
        every { producer.beginTransaction() } returns Unit
        every { producer.abortTransaction() } returns Unit
        every { producer.send(any()) } throws KafkaException()

        invoking { producerWrapper.sendEventsTransactionally(events) } `should throw` RetriableKafkaException::class

        verifyOrder {
            producer.beginTransaction()
            producer.send(any())
            producer.abortTransaction()
        }
    }

    @Test
    fun `Encountering any other Exception during a transaction should close producer and throw an UnretriableKafkaException`() {
        every { producer.beginTransaction() } returns Unit
        every { producer.close() } returns Unit
        every { producer.send(any()) } throws Exception()

        invoking { producerWrapper.sendEventsTransactionally(events) } `should throw` UnretriableKafkaException::class

        verifyOrder {
            producer.beginTransaction()
            producer.send(any())
            producer.close()
        }
    }

    @Test
    fun `Should be able to send events correctly outside a transaction`() {
        every { producer.send(any(), any()) } returns null

        events.forEach { producerWrapper.sendEvent(it.key, it.value) }

        verify(exactly = events.size) { producer.send(any(), any()) }
        verify(exactly = 0) { producer.beginTransaction() }
        verify(exactly = 0) { producer.commitTransaction() }
    }

    @Test
    fun `Should throw RetriableKafkaException if sending event raised a KafkaException`() {

        val handler = slot<Callback>()
        every { producer.send(any(), capture(handler)) } answers {
            handler.captured.onCompletion(null, KafkaException())
            null
        }
        val event = events[1]

        invoking { producerWrapper.sendEvent(event.key, event.value) } `should throw` RetriableKafkaException::class
    }

    @Test
    fun `Should throw UnretriableKafkaException if sending event raised any other Exception`() {

        val handler = slot<Callback>()
        every { producer.send(any(), capture(handler)) } answers {
            handler.captured.onCompletion(null, Exception())
            null
        }
        val event = events[1]

        invoking { producerWrapper.sendEvent(event.key, event.value) } `should throw` UnretriableKafkaException::class
    }
}