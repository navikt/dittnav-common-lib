package no.nav.personbruker.dittnav.common.security

import no.nav.personbruker.dittnav.common.security.TokenValidationContextPrincipalObjectMother.createPrincipalForAzure
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.ktor.TokenValidationContextPrincipal
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test
import java.util.NoSuchElementException
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldNotBeNullOrBlank

internal class AuthenticatedUserFactoryTest {

    @Test
    fun `should throw exception if principal is missing`() {
        invoking {
            AuthenticatedUserFactory.createNewAuthenticatedUser(null)
        } `should throw` Exception::class
    }

    @Test
    fun `should throw exception if the token context is empty`() {
        val context = TokenValidationContext(emptyMap())
        val principal = TokenValidationContextPrincipal(context)
        invoking {
            AuthenticatedUserFactory.createNewAuthenticatedUser(principal)
        } `should throw` NoSuchElementException::class
    }

    @Test
    fun `should extract identity from the claim with the name sub for tokens form Azure`() {
        val expectedIdent = "000"
        val expectedLoginLevel = 3

        val principal = createPrincipalForAzure(expectedIdent, expectedLoginLevel)


        val authenticatedUser = AuthenticatedUserFactory.createNewAuthenticatedUser(principal)

        authenticatedUser.ident `should be equal to` expectedIdent
        authenticatedUser.loginLevel `should be equal to` expectedLoginLevel
        authenticatedUser.token.shouldNotBeNullOrBlank()
    }
}