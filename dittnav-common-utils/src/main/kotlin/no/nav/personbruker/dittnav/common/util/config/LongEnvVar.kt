package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList

object LongEnvVar {
    fun getEnvVarAsLong(varName: String, default: Long? = null): Long
            = getEnvVarAsType(varName, String::toLong, default)

    fun getOptionalEnvVarAsLong(varName: String, default: Long? = null): Long?
            = getOptionalEnvVarAsType(varName, String::toLong, default)

    fun getEnvVarAsLongList(varName: String, default: List<Long>? = null, separator: String = ","): List<Long>
            = getEnvVarAsTypedList(varName, String::toLong, default, separator)

    fun getOptionalEnvVarAsLongList(varName: String, default: List<Long>? = null, separator: String = ","): List<Long>
            = getOptionalEnvVarAsTypedList(varName, String::toLong, default, separator)
}