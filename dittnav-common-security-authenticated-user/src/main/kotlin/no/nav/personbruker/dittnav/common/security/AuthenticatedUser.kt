package no.nav.personbruker.dittnav.common.security

import java.time.Instant
import java.time.temporal.ChronoUnit

data class AuthenticatedUser (
    val ident: String,
    val loginLevel: Int,
    val token: String,
    val tokenExpirationTime: Instant,
    val auxiliaryEssoToken: String? = null
) {

    fun createAuthenticationHeader(): String {
        return "Bearer $token"
    }

    override fun toString(): String {
        return "AuthenticatedUser(ident='***', loginLevel=$loginLevel, token='***', expiryTime=$tokenExpirationTime, expired=${isTokenExpired()})"
    }

    fun isTokenExpired(): Boolean {
        val now = Instant.now()
        return tokenExpirationTime.isBefore(now)
    }

    fun isTokenAboutToExpire(expiryThresholdInSeconds: Long): Boolean {
        val now = Instant.now()
        return now.isAfter(tokenExpirationTime.minus(expiryThresholdInSeconds, ChronoUnit.SECONDS))
    }
}
