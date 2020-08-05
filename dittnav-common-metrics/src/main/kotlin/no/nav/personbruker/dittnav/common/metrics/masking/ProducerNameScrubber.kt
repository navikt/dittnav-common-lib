package no.nav.personbruker.dittnav.common.metrics.masking

class ProducerNameScrubber(private val producerNameResolver: PublicAliasResolver,
                           private val defaultUser: String = "unknown-user",
                           private val defaultSystemUser: String = "unmapped-system-user") {

    suspend fun getPublicAlias(systembruker: String): String {
        return producerNameResolver.getProducerNameAlias(systembruker) ?: findFallBackAlias(systembruker)
    }

    private fun findFallBackAlias(systembruker: String): String {
        return if (isSystemUser(systembruker)) {
            defaultSystemUser
        } else {
            defaultUser
        }
    }

    private fun isSystemUser(producer: String): Boolean {
        return "^srv.{1,12}\$".toRegex().matches(producer)
    }
}
