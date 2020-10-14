package no.nav.personbruker.dittnav.common.util.database.fetching

import java.sql.ResultSet
import java.sql.SQLException

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