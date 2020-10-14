package no.nav.personbruker.dittnav.common.util.config

import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

internal object TypedEnvVar {
    internal inline fun <reified T> getEnvVarAsType(varName: String, mapper: (String) -> T, default: T? = null): T {
        return SystemWrapper.getEnvVar(varName)
            ?.applyMapper(mapper)
            ?: default
            ?: throw IllegalStateException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
    }

    internal inline fun <reified T> getOptionalEnvVarAsType(varName: String, mapper: (String) -> T, default: T? = null): T? {
        return SystemWrapper.getEnvVar(varName)
            ?.applyMapper(mapper)
            ?: default
    }

    internal inline fun <reified T> getEnvVarAsTypedList(varName: String, mapper: (String) -> T, default: List<T>? = null, separator: String = ","): List<T> {
        return SystemWrapper.getEnvVar(varName)
            ?.split(separator)
            ?.map { listEntry -> listEntry.applyMapper(mapper) }
            ?: default
            ?: throw IllegalStateException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
    }

    internal inline fun <reified T> getOptionalEnvVarAsTypedList(varName: String, mapper: (String) -> T, default: List<T>? = null, separator: String = ","): List<T> {
        return SystemWrapper.getEnvVar(varName)
            ?.split(separator)
            ?.map { listEntry -> listEntry.applyMapper(mapper) }
            ?: default
            ?: emptyList()
    }

    private inline fun <reified T> String.applyMapper(mapper: (String) -> T): T {
        return try {
            mapper(this)
        } catch (e: Exception) {
            throw IllegalArgumentException("Klarte ikke konvertere variabel $this til ${T::class.simpleName}")
        }
    }
}