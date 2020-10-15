package no.nav.personbruker.dittnav.common.security

import io.ktor.application.*
import io.mockk.mockk
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class AuthenticatedUserTest {

    @Test
    fun `should return expected values`() {
        val expectedIdent = "12345"
        val expectedLoginLevel = 4

        val authenticatedUser = AuthenticatedUserObjectMother.createAuthenticatedUser(expectedIdent, expectedLoginLevel)

        authenticatedUser.ident `should be equal to` expectedIdent
        authenticatedUser.loginLevel `should be equal to` expectedLoginLevel
        authenticatedUser.token.shouldNotBeNullOrBlank()
    }

    @Test
    fun `should create authentication header`() {
        val authenticatedUser = AuthenticatedUserObjectMother.createAuthenticatedUser()

        val generatedAuthHeader = authenticatedUser.createAuthenticationHeader()

        generatedAuthHeader `should be equal to` "Bearer ${authenticatedUser.token}"
    }

    @Test
    fun `should not include sensitive values in the output for the toString method`() {
        val authenticatedUser = AuthenticatedUserObjectMother.createAuthenticatedUser()

        val outputOfToString = authenticatedUser.toString()

        outputOfToString `should contain` authenticatedUser.loginLevel.toString()
        outputOfToString `should not contain` authenticatedUser.ident
        outputOfToString `should not contain` authenticatedUser.token
    }

    @Test
    fun `Should recognize when is expired`() {
        val momentInPast = Instant.now().minus(5, ChronoUnit.MINUTES)
        val momentInFuture = Instant.now().plus(5, ChronoUnit.MINUTES)

        val expectedExpired = AuthenticatedUser("", 0, "", momentInPast)
        val expectedNotExpired = AuthenticatedUser("", 0, "", momentInFuture)

        expectedExpired.isTokenExpired() `should be` true
        expectedNotExpired.isTokenExpired() `should be` false
    }
    @Test
    fun `Should recognize when token expiry is past a certain threshold`() {
        val moment1 = Instant.now().plus(4, ChronoUnit.MINUTES)
        val moment2 = Instant.now().plus(6, ChronoUnit.MINUTES)

        val expectedExpiring = AuthenticatedUser("", 0, "", moment1)
        val expectedNotExpiring = AuthenticatedUser("", 0, "", moment2)

        expectedExpiring.isTokenAboutToExpire(5) `should be` true
        expectedNotExpiring.isTokenAboutToExpire(5) `should be` false
    }
}