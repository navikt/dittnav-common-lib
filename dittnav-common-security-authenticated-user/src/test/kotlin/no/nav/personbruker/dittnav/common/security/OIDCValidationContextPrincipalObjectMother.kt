package no.nav.personbruker.dittnav.common.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.ktor.TokenValidationContextPrincipal
import java.security.Key
import java.time.Instant
import java.util.*

object TokenValidationContextPrincipalObjectMother {

    fun createPrincipalForAzure(ident: String, loginLevel: Int): TokenValidationContextPrincipal {
        val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        val inTwoMinutes = Date(Instant.now().plusSeconds(60).toEpochMilli())
        val expiredJws = Jwts.builder()
            .setSubject(ident)
            .setExpiration(inTwoMinutes)
            .addClaims(
                mutableMapOf(
                    Pair("acr", "Level$loginLevel")
                ) as Map<String, Any>?)
            .signWith(key).compact()

        val token = JwtToken(expiredJws)

        val issuerShortNameValidatedTokenMap = mutableMapOf<String, JwtToken>()
        val issuer = "keyForIssuer"
        issuerShortNameValidatedTokenMap[issuer] = token
        val context = TokenValidationContext(issuerShortNameValidatedTokenMap)

        return TokenValidationContextPrincipal(context)
    }
}