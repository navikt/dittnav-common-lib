package no.nav.personbruker.dittnav.common.util.database.updating

import java.sql.Connection
import java.sql.PreparedStatement

fun Connection.executeBatchUpdateQuery(sql: String, paramInit: PreparedStatement.() -> Unit) {
    autoCommit = false

    prepareStatement(sql).use { statement ->
        statement.paramInit()
        statement.executeBatch()
    }

    commit()
}
