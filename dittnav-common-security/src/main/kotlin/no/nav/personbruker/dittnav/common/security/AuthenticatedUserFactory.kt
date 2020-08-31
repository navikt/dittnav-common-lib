package no.nav.personbruker.dittnav.common.security

import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.ktor.TokenValidationContextPrincipal
import java.time.LocalDateTime
import java.time.ZoneId

object AuthenticatedUserFactory {

    private val IDENT_CLAIM: IdentityClaim
    private val defaultClaim = IdentityClaim.SUBJECT
    private val oidcIdentityClaimName = "OIDC_CLAIM_CONTAINING_THE_IDENTITY"

    init {
        val identityClaimFromEnvVariable = System.getenv(oidcIdentityClaimName) ?: defaultClaim.claimName
        IDENT_CLAIM = IdentityClaim.fromClaimName(identityClaimFromEnvVariable)
    }

    fun createNewAuthenticatedUser(principal: TokenValidationContextPrincipal?): AuthenticatedUser {
        val token = principal?.context?.firstValidToken?.get()
            ?: throw Exception("Det ble ikke funnet noe token. Dette skal ikke kunne skje.")

        val ident: String = token.jwtTokenClaims.getStringClaim(IDENT_CLAIM.claimName)
        val loginLevel =
            extractLoginLevel(
                token
            )
        val expirationTime =
            getTokenExpirationLocalDateTime(
                token
            )

        return AuthenticatedUser(ident, loginLevel, token.tokenAsString, expirationTime)
    }

    private fun extractLoginLevel(token: JwtToken): Int {

        return when (token.jwtTokenClaims.getStringClaim("acr")) {
            "Level3" -> 3
            "Level4" -> 4
            else -> throw Exception("Innloggingsnivå ble ikke funnet. Dette skal ikke kunne skje.")
        }
    }

    private fun getTokenExpirationLocalDateTime(token: JwtToken): LocalDateTime {
        return token.jwtTokenClaims
            .expirationTime
            .toInstant()
            .atZone(ZoneId.of("Europe/Oslo"))
            .toLocalDateTime()
    }

}
