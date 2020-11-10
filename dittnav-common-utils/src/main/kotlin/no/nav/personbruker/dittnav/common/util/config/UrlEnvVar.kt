package no.nav.personbruker.dittnav.common.util.config

import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import java.net.URL

object UrlEnvVar {
    fun getEnvVarAsURL(varName: String, default: URL? = null, trimTrailingSlash: Boolean = false): URL {
        return getEnvVarAsType(varName, default) { envVar ->
            if (trimTrailingSlash) {
                URL(envVar.trimEnd('/'))
            } else {
                URL(envVar)
            }
        }
    }

    fun getOptionalEnvVarAsURL(varName: String, default: URL? = null, trimTrailingSlash: Boolean = false): URL? {
        return getOptionalEnvVarAsType(varName, default) { envVar ->
            if (trimTrailingSlash) {
                URL(envVar.trimEnd('/'))
            } else {
                URL(envVar)
            }
        }
    }
}