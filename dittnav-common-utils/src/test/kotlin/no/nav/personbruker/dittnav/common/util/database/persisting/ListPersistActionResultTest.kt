package no.nav.personbruker.dittnav.common.util.database.persisting

import no.nav.personbruker.dittnav.common.util.database.persisting.PersistFailureReason.*
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test


internal class ListPersistActionResultTest {

    @Test
    fun `Function mapParamListToResultArray should map persist result correctly`() {
        val toPersist = listOf(1, 2, 3, 4)

        val resultArray = listOf(1, 1, 0, 1).toIntArray()

        val result = ListPersistActionResult.mapParamListToResultArray(toPersist, resultArray)

        result.allEntitiesPersisted() `should equal` false
        result.getPersistedEntitites() `should equal` listOf(1, 2, 4)
        result.getConflictingEntities() `should equal` listOf(3)
    }

    @Test
    fun `Function mapListOfIndividualResults should map persist result correctly`() {
        val resultList = listOf(
            1 to NO_ERROR,
            2 to UNKNOWN,
            3 to CONFLICTING_KEYS,
            4 to NO_ERROR
        )

        val result = ListPersistActionResult.mapListOfIndividualResults(resultList)

        result.allEntitiesPersisted() `should equal` false
        result.getPersistedEntitites() `should equal` listOf(1, 4)
        result.getConflictingEntities() `should equal` listOf(3)
    }
}