package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList

object LongEnvVar {
    fun getEnvVarAsLong(varName: String, default: Long? = null): Long
            = getEnvVarAsType(varName, default, String::toLong)

    fun getOptionalEnvVarAsLong(varName: String, default: Long? = null): Long?
            = getOptionalEnvVarAsType(varName, default, String::toLong)

    fun getEnvVarAsLongList(varName: String, default: List<Long>? = null, separator: String = ","): List<Long>
            = getEnvVarAsTypedList(varName, default, separator, String::toLong)

    fun getOptionalEnvVarAsLongList(varName: String, default: List<Long>? = null, separator: String = ","): List<Long>
            = getOptionalEnvVarAsTypedList(varName, default, separator, String::toLong)
}