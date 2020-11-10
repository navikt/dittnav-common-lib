package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList

object IntEnvVar {
    fun getEnvVarAsInt(varName: String, default: Int? = null): Int
            = getEnvVarAsType(varName, default, String::toInt)

    fun getOptionalEnvVarAsInt(varName: String, default: Int? = null): Int?
            = getOptionalEnvVarAsType(varName, default, String::toInt)

    fun getEnvVarAsIntList(varName: String, default: List<Int>? = null, separator: String = ","): List<Int>
            = getEnvVarAsTypedList(varName, default, separator, String::toInt)

    fun getOptionalEnvVarAsIntList(varName: String, default: List<Int>? = null, separator: String = ","): List<Int>
            = getOptionalEnvVarAsTypedList(varName, default, separator, String::toInt)
}