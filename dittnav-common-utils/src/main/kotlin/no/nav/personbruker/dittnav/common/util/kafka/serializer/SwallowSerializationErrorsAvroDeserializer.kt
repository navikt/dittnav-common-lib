package no.nav.personbruker.dittnav.common.util.kafka.serializer

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import org.apache.kafka.common.errors.SerializationException
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Avro deserialiserer som returnerer `null` hvis den mottar bytes som ikke kan deserialiseres til en Avro-type.
 *
 * Denne implementasjonen er laget for å kunne ignorere mottatte eventer som ikke lar seg deserialisere, det vil
 * bare logges at et event ikke lot seg deserialisere og det returneres `null`. Dette gjør at appens pollere ikke
 * stopper selv om det skulle komme et event som ikke lar seg deserialisere.
 */
class SwallowSerializationErrorsAvroDeserializer: KafkaAvroDeserializer {
    constructor() : super()
    constructor(schemaRegistryClient: SchemaRegistryClient) : super(schemaRegistryClient)
    constructor(schemaRegistryClient: SchemaRegistryClient, pros: MutableMap<String, Any>) : super(schemaRegistryClient, pros)

    private val log: Logger = LoggerFactory.getLogger(SwallowSerializationErrorsAvroDeserializer::class.java)

    override fun deserialize(bytes: ByteArray): Any? {
        var result: Any? = null
        try {
            result = super.deserialize(bytes)

        } catch (e: SerializationException) {
            val msg = "Eventet kunne ikke deserialiseres, og blir forkastet. Dette skjedde mest sannsynlig fordi eventet ikke var i henold til Avro-skjemaet for denne topic-en."
            log.error(msg, e)
        }
        return result
    }
}
