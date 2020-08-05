package no.nav.personbruker.dittnav.common.util.database.fetching

import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime

fun <T> ResultSet.mapSingleResult(result: ResultSet.() -> T): T =
    if (next()) {
        result()
    } else {
        throw SQLException("Found no rows")
    }

fun <T> ResultSet.mapList(result: ResultSet.() -> T): List<T> =
    mutableListOf<T>().apply {
        while (next()) {
            add(result())
        }
    }

fun ResultSet.getUtcDateTime(columnLabel: String): LocalDateTime = getTimestamp(columnLabel).toLocalDateTime()

fun ResultSet.getEpochTimeInSeconds(label: String): Long {
    return getTimestamp(label).toInstant().epochSecond
}