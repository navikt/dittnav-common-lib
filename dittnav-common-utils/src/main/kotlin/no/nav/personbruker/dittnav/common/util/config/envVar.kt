package no.nav.personbruker.dittnav.common.util.config

import java.lang.IllegalStateException

fun getEnvVar(varName: String, default: String? = null): String {
    return SystemWrapper.getEnvVar(varName)
        ?: default
        ?: throw IllegalStateException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}

fun getOptionalEnvVar(varName: String, default: String? = null): String? {
    return SystemWrapper.getEnvVar(varName)
        ?: default
}

fun getEnvVarAsList(varName: String, default: List<String>? = null, separator: String = ","): List<String> {
    return SystemWrapper.getEnvVar(varName)
        ?.split(separator)
        ?: default
        ?: throw IllegalStateException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}

fun getOptionalEnvVarAsList(varName: String, default: List<String>? = null, separator: String = ","): List<String> {
    return SystemWrapper.getEnvVar(varName)
        ?.split(separator)
        ?: default
        ?: emptyList()
}

internal object SystemWrapper {
    fun getEnvVar(varName: String): String? {
        return System.getenv(varName)
    }
}