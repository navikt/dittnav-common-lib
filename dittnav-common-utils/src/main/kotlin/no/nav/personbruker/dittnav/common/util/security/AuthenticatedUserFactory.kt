package no.nav.personbruker.dittnav.common.util.security

import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.ktor.OIDCValidationContextPrincipal

object AuthenticatedUserFactory {

    private val IDENT_CLAIM: IdentityClaim
    private val defaultClaim = IdentityClaim.SUBJECT
    private val oidcIdentityClaimName = "OIDC_CLAIM_CONTAINING_THE_IDENTITY"

    init {
        val identityClaimFromEnvVariable = System.getenv(oidcIdentityClaimName) ?: defaultClaim.claimName
        IDENT_CLAIM = IdentityClaim.fromClaimName(identityClaimFromEnvVariable)
    }

    fun createNewAuthenticatedUser(principal: OIDCValidationContextPrincipal?): AuthenticatedUser {
        val token = principal?.context?.firstValidToken?.get()
                ?: throw Exception("Det ble ikke funnet noe token. Dette skal ikke kunne skje.")

        val ident: String = token.jwtTokenClaims.getStringClaim(IDENT_CLAIM.claimName)
        val loginLevel = extractLoginLevel(token)

        return AuthenticatedUser(ident, loginLevel, token.tokenAsString)
    }

    private fun extractLoginLevel(token: JwtToken): Int {
        val innloggingsnivaaClaim = token.jwtTokenClaims.getStringClaim("acr")

        return when (innloggingsnivaaClaim) {
            "Level3" -> 3
            "Level4" -> 4
            else -> throw Exception("Innloggingsnivå ble ikke funnet. Dette skal ikke kunne skje.")
        }
    }

}
