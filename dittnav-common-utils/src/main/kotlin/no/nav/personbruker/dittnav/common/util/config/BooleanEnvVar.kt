package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList

object BooleanEnvVar {
    fun getEnvVarAsBoolean(varName: String, default: Boolean? = null): Boolean
            = getEnvVarAsType(varName, default, String::toBoolean)

    fun getOptionalEnvVarAsBoolean(varName: String, default: Boolean? = null): Boolean?
            = getOptionalEnvVarAsType(varName, default, String::toBoolean)

    fun getEnvVarAsBooleanList(varName: String, default: List<Boolean>? = null, separator: String = ","): List<Boolean>
            = getEnvVarAsTypedList(varName, default, separator, String::toBoolean)

    fun getOptionalEnvVarAsBooleanList(varName: String, default: List<Boolean>? = null, separator: String = ","): List<Boolean>
            = getOptionalEnvVarAsTypedList(varName, default, separator, String::toBoolean)
}