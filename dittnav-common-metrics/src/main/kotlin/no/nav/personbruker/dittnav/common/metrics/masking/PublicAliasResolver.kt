package no.nav.personbruker.dittnav.common.metrics.masking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.time.LocalDateTime

class PublicAliasResolver(private val aliasMappingProvider: suspend () -> List<NameAndPublicAlias>,
                          private val cacheLifespanMinutes: Long = 15) {

    private var publicNameAliases: Map<String, String> = emptyMap()
    private var timeLastRetrieved: LocalDateTime? = null

    private val log: Logger = LoggerFactory.getLogger(PublicAliasResolver::class.java)

    suspend fun getProducerNameAlias(systembruker: String): String? {
        val containsAlias = publicNameAliases.containsKey(systembruker)
        if(shouldFetchNewValuesFromDB() || !containsAlias) {
            withContext(Dispatchers.IO) {
                updateCache()
            }
            if(!containsAlias) {
                log.warn("Manglet alias for oppgitt systembruker, forsøker å oppdatere cache på nytt.")
            }
        }
        return publicNameAliases[systembruker]
    }

    private suspend fun updateCache() {
        publicNameAliases = populateProducerNameCache()
        timeLastRetrieved = LocalDateTime.now()
    }

    private fun shouldFetchNewValuesFromDB(): Boolean {
        return publicNameAliases.isEmpty() ||
                timeLastRetrieved == null ||
                cacheIsExpired()
    }

    private suspend fun populateProducerNameCache(): Map<String, String> {
        return try {
            val aliasMappingList = aliasMappingProvider.invoke()
            aliasMappingList.map { it.name to it.publicAlias }.toMap()
        } catch(e: Exception) {
            log.error("En feil oppstod ved henting av produsentnavn, har ikke oppdatert cache med verdier.", e)
            publicNameAliases
        }
    }

    private fun cacheIsExpired(): Boolean {
        return LocalDateTime.now().isAfter(timeLastRetrieved?.plusMinutes(cacheLifespanMinutes))
    }
}
