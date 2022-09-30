package no.nav.personbruker.dittnav.common.util.config

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UrlEnvVarTest {

    private val envVarName = "DOMAIN_URL"

    @BeforeEach
    fun setupMock() {
        mockkObject(SystemWrapper)
    }

    @AfterEach
    fun cleanUp() {
        unmockkObject(SystemWrapper)
    }

    @Test
    fun `Function getEnvVarAsURL should return valid url`() {
        val urlString = "http://domain.com"

        every { SystemWrapper.getEnvVar(envVarName) } returns urlString

        val url = UrlEnvVar.getEnvVarAsURL(envVarName)

        url.toString() shouldBe urlString
    }

    @Test
    fun `Function getEnvVarAsURL should trim trailing slash if requested, and should default to false`() {
        val urlString = "http://domain.com/"

        val urlStringTrimmed = "http://domain.com"

        every { SystemWrapper.getEnvVar(envVarName) } returns urlString

        val urlDefault = UrlEnvVar.getEnvVarAsURL(envVarName)
        val urlUntrimmed = UrlEnvVar.getEnvVarAsURL(envVarName, trimTrailingSlash = false)
        val urlTrimmed = UrlEnvVar.getEnvVarAsURL(envVarName, trimTrailingSlash = true)

        urlDefault.toString() shouldBe urlString
        urlUntrimmed.toString() shouldBe urlString
        urlTrimmed.toString() shouldBe urlStringTrimmed
    }

    @Test
    fun `Function getEnvVarAsURL should throw exception if url is malformed`() {
        val urlString = "domain.com"

        every { SystemWrapper.getEnvVar(envVarName) } returns urlString

        shouldThrowAny {
            UrlEnvVar.getEnvVarAsURL(envVarName)
        }
    }

    @Test
    fun `Function getOptionalEnvVarAsURL should return valid url`() {
        val urlString = "http://domain.com"

        every { SystemWrapper.getEnvVar(envVarName) } returns urlString

        val url = UrlEnvVar.getOptionalEnvVarAsURL(envVarName)

        url.toString() shouldBe urlString
    }

    @Test
    fun `Function getOptionalEnvVarAsURL should trim trailing slash if requested, and should default to false`() {
        val urlString = "http://domain.com/"

        val urlStringTrimmed = "http://domain.com"

        every { SystemWrapper.getEnvVar(envVarName) } returns urlString

        val urlDefault = UrlEnvVar.getOptionalEnvVarAsURL(envVarName)
        val urlUntrimmed = UrlEnvVar.getOptionalEnvVarAsURL(envVarName, trimTrailingSlash = false)
        val urlTrimmed = UrlEnvVar.getOptionalEnvVarAsURL(envVarName, trimTrailingSlash = true)

        urlDefault.toString() shouldBe urlString
        urlUntrimmed.toString() shouldBe urlString
        urlTrimmed.toString() shouldBe urlStringTrimmed
    }

    @Test
    fun `Function getOptionalEnvVarAsURL should throw exception if url is malformed`() {
        val urlString = "domain.com"

        every { SystemWrapper.getEnvVar(envVarName) } returns urlString

        shouldThrowAny {
            UrlEnvVar.getOptionalEnvVarAsURL(envVarName)
        }
    }
}
