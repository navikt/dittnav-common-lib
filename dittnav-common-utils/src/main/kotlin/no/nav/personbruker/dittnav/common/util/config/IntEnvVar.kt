package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList

object IntEnvVar {
    fun getEnvVarAsInt(varName: String, default: Int? = null): Int
            = getEnvVarAsType(varName, String::toInt, default)

    fun getOptionalEnvVarAsInt(varName: String, default: Int? = null): Int?
            = getOptionalEnvVarAsType(varName, String::toInt, default)

    fun getEnvVarAsIntList(varName: String, default: List<Int>? = null, separator: String = ","): List<Int>
            = getEnvVarAsTypedList(varName, String::toInt, default, separator)

    fun getOptionalEnvVarAsIntList(varName: String, default: List<Int>? = null, separator: String = ","): List<Int>
            = getOptionalEnvVarAsTypedList(varName, String::toInt, default, separator)
}