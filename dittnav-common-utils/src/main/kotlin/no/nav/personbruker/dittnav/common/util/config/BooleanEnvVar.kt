package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList

object BooleanEnvVar {
    fun getEnvVarAsBoolean(varName: String, default: Boolean? = null): Boolean
            = getEnvVarAsType(varName, String::toBoolean, default)

    fun getOptionalEnvVarAsBoolean(varName: String, default: Boolean? = null): Boolean?
            = getOptionalEnvVarAsType(varName, String::toBoolean, default)

    fun getEnvVarAsBooleanList(varName: String, default: List<Boolean>? = null, separator: String = ","): List<Boolean>
            = getEnvVarAsTypedList(varName, String::toBoolean, default, separator)

    fun getOptionalEnvVarAsBooleanList(varName: String, default: List<Boolean>? = null, separator: String = ","): List<Boolean>
            = getOptionalEnvVarAsTypedList(varName, String::toBoolean, default, separator)
}