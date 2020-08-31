package no.nav.personbruker.dittnav.common.security

import java.time.LocalDateTime

data class AuthenticatedUser(val ident: String,
                             val loginLevel: Int,
                             val token: String,
                             val tokenExpirationTime: LocalDateTime) {

    fun createAuthenticationHeader(): String {
        return "Bearer $token"
    }

    override fun toString(): String {
        return "AuthenticatedUser(ident='***', loginLevel=$loginLevel, token='***', expiryTime=$tokenExpirationTime)"
    }

}
