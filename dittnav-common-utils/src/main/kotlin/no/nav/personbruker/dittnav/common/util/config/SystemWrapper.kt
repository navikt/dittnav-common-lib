package no.nav.personbruker.dittnav.common.util.config

internal object SystemWrapper {
    fun getEnvVar(varName: String): String? {
        return System.getenv(varName)
    }
}