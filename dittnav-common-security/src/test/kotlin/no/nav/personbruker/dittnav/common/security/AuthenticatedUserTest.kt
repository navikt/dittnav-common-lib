package no.nav.personbruker.dittnav.common.security

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.junit.jupiter.api.Test

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
}