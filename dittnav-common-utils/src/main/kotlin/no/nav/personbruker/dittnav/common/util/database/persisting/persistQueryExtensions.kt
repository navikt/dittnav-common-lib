package no.nav.personbruker.dittnav.common.util.database.persisting

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

fun Connection.executePersistQuery(sql: String, skipConflicting: Boolean = true, paramInit: PreparedStatement.() -> Unit): PersistActionResult {

    val finalSqlString = appendSkipStatementIfRequested(sql, skipConflicting)

    return prepareStatement(finalSqlString, Statement.RETURN_GENERATED_KEYS).use {

        it.paramInit()
        it.executeUpdate()

        if (it.generatedKeys.next()) {
            PersistActionResult.success(it.generatedKeys.getInt("id"))
        } else {
            PersistActionResult.failure(PersistFailureReason.CONFLICTING_KEYS)
        }
    }
}

fun Connection.executeBatchPersistQuery(sql: String, skipConflicting: Boolean = true, paramInit: PreparedStatement.() -> Unit): IntArray {
    autoCommit = false

    val finalSqlString = appendSkipStatementIfRequested(sql, skipConflicting)

    val result = prepareStatement(finalSqlString).use { statement ->
        statement.paramInit()
        statement.executeBatch()
    }
    commit()
    return result
}

fun <T> IntArray.toBatchPersistResult(paramList: List<T>) = ListPersistActionResult.mapParamListToResultArray(paramList, this)

inline fun <T> List<T>.persistEachIndividuallyAndAggregateResults(persistAction: (T) -> PersistActionResult): ListPersistActionResult<T> {
    return map { entity ->
        entity to persistAction(entity).persistOutcome
    }.let { aggregate ->
        ListPersistActionResult.mapListOfIndividualResults(aggregate)
    }
}


private fun appendSkipStatementIfRequested(baseSql: String, skipConflicting: Boolean) =
    if (skipConflicting) {
        """$baseSql ON CONFLICT DO NOTHING"""
    } else {
        baseSql
    }
