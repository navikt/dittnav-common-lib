package no.nav.personbruker.dittnav.common.util.database.updating

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.PreparedStatement


internal class UpdateActionExtensionsKtTest {
    private val connection: Connection = mockk()
    private val statement: PreparedStatement = mockk()

    private val query = "update table .."


    @Test
    fun `Function executeBatchUpdateQuery should prepare statement, then initialize params and run it, then close statement and commit`() {
        val paramInit: PreparedStatement.() -> Unit = {}

        every { connection.setAutoCommit(false) } returns Unit
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeBatch() } returns IntArray(0)
        every { statement.close() } returns Unit
        every { connection.commit() } returns Unit

        connection.executeBatchUpdateQuery(query) { paramInit() }

        verifyOrder {
            connection.setAutoCommit(false)
            connection.prepareStatement(any())
            statement.paramInit()
            statement.executeBatch()
            statement.close()
            connection.commit()
        }
    }
}