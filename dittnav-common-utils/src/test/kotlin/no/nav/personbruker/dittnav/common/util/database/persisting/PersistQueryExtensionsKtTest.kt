package no.nav.personbruker.dittnav.common.util.database.persisting

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyOrder
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not be equal to`
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

internal class PersistQueryExtensionsKtTest {

    private val connection: Connection = mockk()
    private val statement: PreparedStatement = mockk()

    private val query = "insert into .."

    private val entityId = 123

    @Test
    fun `Function executePersistQuery should prepare statement, then initialize params and run it, then close statement`() {
        val paramInit: PreparedStatement.() -> Unit = {}

        every { connection.prepareStatement(any(), Statement.RETURN_GENERATED_KEYS) } returns statement
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys.next() } returns true
        every { statement.generatedKeys.getInt("id") } returns entityId
        every { statement.close() } returns Unit

        val expected = PersistActionResult.success(entityId)
        val result = connection.executePersistQuery(query) { paramInit() }

        verifyOrder {
            connection.prepareStatement(any(), Statement.RETURN_GENERATED_KEYS)
            statement.paramInit()
            statement.executeUpdate()
            statement.generatedKeys.next()
            statement.generatedKeys.getInt("id")
            statement.close()
        }

        result `should equal` expected
    }
    @Test
    fun `Function executePersistQuery should return error result if no change was made`() {
        val paramInit: PreparedStatement.() -> Unit = {}

        every { connection.prepareStatement(any(), Statement.RETURN_GENERATED_KEYS) } returns statement
        every { statement.executeUpdate() } returns 0
        every { statement.generatedKeys.next() } returns false
        every { statement.close() } returns Unit

        val expected = PersistActionResult.failure(PersistFailureReason.CONFLICTING_KEYS)
        val result = connection.executePersistQuery(query) { paramInit() }

        verifyOrder {
            connection.prepareStatement(any(), Statement.RETURN_GENERATED_KEYS)
            statement.paramInit()
            statement.executeUpdate()
            statement.generatedKeys.next()
            statement.close()
        }

        result `should equal` expected
    }

    @Test
    fun `Function executePersistQuery should append skip statement only when requested`() {
        val paramInit: PreparedStatement.() -> Unit = {}

        val queryCapture = slot<String>()
        every { connection.prepareStatement(capture(queryCapture), Statement.RETURN_GENERATED_KEYS) } returns statement
        every { statement.executeUpdate() } returns 0
        every { statement.generatedKeys.next() } returns false
        every { statement.close() } returns Unit

        connection.executePersistQuery(query, false) { paramInit() }
        queryCapture.captured `should equal` query

        connection.executePersistQuery(query, true) { paramInit() }
        queryCapture.captured `should not be equal to` query
        queryCapture.captured `should contain` query
    }

    @Test
    fun `Function executeBatchPersistQuery should prepare statement, then initialize params and run it, then close statement and commit`() {
        val paramInit: PreparedStatement.() -> Unit = {}

        val persistResult = listOf(1, 1, 0, 0, 1).toIntArray()

        every { connection.setAutoCommit(false) } returns Unit
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeBatch() } returns persistResult
        every { statement.close() } returns Unit
        every { connection.commit() } returns Unit

        val result = connection.executeBatchPersistQuery(query) { paramInit() }

        verifyOrder {
            connection.prepareStatement(any())
            statement.paramInit()
            statement.executeBatch()
            statement.close()
            connection.commit()
        }

        result `should equal` persistResult
    }

    @Test
    fun `Function executeBatchPersistQuery should append skip statement only when requested`() {
        val paramInit: PreparedStatement.() -> Unit = {}

        val persistResult = listOf(1, 1, 0, 0, 1).toIntArray()

        val queryCapture = slot<String>()
        every { connection.setAutoCommit(false) } returns Unit
        every { connection.prepareStatement(capture(queryCapture)) } returns statement
        every { statement.executeBatch() } returns persistResult
        every { statement.close() } returns Unit
        every { connection.commit() } returns Unit

        connection.executeBatchPersistQuery(query, false) { paramInit() }
        queryCapture.captured `should equal` query

        connection.executeBatchPersistQuery(query, true) { paramInit() }
        queryCapture.captured `should not be equal to` query
        queryCapture.captured `should contain` query
    }


}