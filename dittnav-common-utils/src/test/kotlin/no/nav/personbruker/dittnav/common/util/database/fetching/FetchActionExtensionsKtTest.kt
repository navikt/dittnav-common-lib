package no.nav.personbruker.dittnav.common.util.database.fetching

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.sql.SQLException

internal class FetchActionExtensionsKtTest {
    private val idColumn = "id"
    private val nameColumn = "name"

    private val person1 = Person(123, "Name Nameson")
    private val person2 = Person(456, "Other Otter")
    private val person3 = Person(789, "First Last")

    @Test
    fun `Function mapSingleResult should return a single result if applicable`() {
        val resultSet = createMockedResultSet()

        val result = resultSet.mapSingleResult {
            toPerson()
        }

        result `should equal` person1
    }

    @Test
    fun `Function mapSingleResult should throw exception if list was empty`() {
        val resultSet: ResultSet = mockk()

        every { resultSet.next() } returns false

        invoking { resultSet.mapSingleResult { toPerson() } } `should throw` SQLException::class
    }

    @Test
    fun `Function mapList should return fetched results`() {
        val resultSet = createMockedResultSet()

        val result = resultSet.mapList {
            toPerson()
        }

        result `should equal` listOf(person1, person2, person3)
    }

    @Test
    fun `Function mapList should return empty list if no results were found`() {
        val resultSet: ResultSet = mockk()

        every { resultSet.next() } returns false

        val result = resultSet.mapList {
            toPerson()
        }

        result `should equal` emptyList()
    }

    private fun createMockedResultSet(): ResultSet {
        val table = listOf(
            listOf(
                idColumn to person1.id.toString(),
                nameColumn to person1.name
            ).toMap(),
            listOf(
                idColumn to person2.id.toString(),
                nameColumn to person2.name
            ).toMap(),
            listOf(
                idColumn to person3.id.toString(),
                nameColumn to person3.name
            ).toMap()
        )

        return ResultSetMock.createResultSetMock(table)
    }

    private fun ResultSet.toPerson(): Person {
        return Person(
            getLong(idColumn),
            getString(nameColumn)
        )
    }

    private data class Person(
        val id: Long,
        val name: String
    )
}