package no.nav.personbruker.dittnav.common.util.kafka.serializer

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericRecordBuilder
import org.junit.jupiter.api.Test


class SwallowSerializationErrorsAvroDeserializerTest {

    private val topic = "dummyTopic"
    private val config = mutableMapOf<String, Any>()
    private val schemaRegistryClient: SchemaRegistryClient
    private val serializer: KafkaAvroSerializer
    private val deserializer: SwallowSerializationErrorsAvroDeserializer

    init {
        config[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "thisUrlMustBeSetAtLeastToADummyValue"

        schemaRegistryClient = MockSchemaRegistryClient()
        serializer = KafkaAvroSerializer(schemaRegistryClient, config)
        deserializer = SwallowSerializationErrorsAvroDeserializer(schemaRegistryClient, config)
    }

    private fun createSchema(): Schema {
        return SchemaBuilder.builder()
                .record("record")
                .fields()
                .requiredBoolean("isATest")
                .endRecord()
    }

    @Test
    fun `should serialize valid records successfully`() {
        val schema = createSchema()
        val original = GenericRecordBuilder(schema)
                .set("isATest", true)
                .build()
        val serialized = serializer.serialize(topic, original)
        val deserialized  = deserializer.deserialize(topic, serialized)

        deserialized `should be equal to` original
    }

    @Test
    fun `should return null for invalid records`() {
        val invalidSerialisedEvent = ByteArray(10)

        val deserialiedRecord = deserializer.deserialize(topic, invalidSerialisedEvent)

        deserialiedRecord.`should be null`()
    }
}
