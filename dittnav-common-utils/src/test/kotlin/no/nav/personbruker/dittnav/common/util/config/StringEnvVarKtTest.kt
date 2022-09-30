package no.nav.personbruker.dittnav.common.util.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVarAsList
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getOptionalEnvVar
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getOptionalEnvVarAsList
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.IllegalStateException

internal class StringEnvVarKtTest {

    private val envName = "PROPERTY_NAME"

    private val envVal = "someValue"
    private val listEnvVal = "one,two,three"

    @BeforeEach
    fun setupMock() {
        mockkObject(SystemWrapper)
    }

    @AfterEach
    fun cleanUp() {
        unmockkObject(SystemWrapper)
    }

    @Test
    fun `Function getEnvVar should return correct environment variable if it exists`() {
        val default = "otherValue"

        every { SystemWrapper.getEnvVar(envName) } returns envVal

        val result = getEnvVar(envName, default)

        result shouldBe envVal
    }

    @Test
    fun `Function getEnvVar should return default value if provided and variable was not found`() {
        val default = "otherValue"

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getEnvVar(envName, default)

        result shouldBe default
    }

    @Test
    fun `Function getEnvVar should throw exception if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        shouldThrow<IllegalStateException> {
            getEnvVar(envName)
        }
    }

    @Test
    fun `Function getOptionalEnvVar should return correct environment variable if it exists`() {
        val default = "otherValue"

        every { SystemWrapper.getEnvVar(envName) } returns envVal

        val result = getOptionalEnvVar(envName, default)

        result shouldBe envVal
    }

    @Test
    fun `Function getOptionalEnvVar should return default value if provided and variable was not found`() {
        val default = "otherValue"

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVar(envName, default)

        result shouldBe default
    }

    @Test
    fun `Function getOptionalEnvVar should return null if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVar(envName)

        result shouldBe null
    }

    @Test
    fun `Function getEnvVarAsList should return correct environment variable if it exists`() {
        val default = listOf("default")
        val expected = listOf("one", "two", "three")

        every { SystemWrapper.getEnvVar(envName) } returns listEnvVal


        val result = getEnvVarAsList(envName, default)

        result shouldBe expected
    }

    @Test
    fun `Function getEnvVarAsList should return default value if provided and variable was not found`() {
        val default = listOf("default")

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getEnvVarAsList(envName, default)

        result shouldBe default
    }

    @Test
    fun `Function getEnvVarAsList should throw exception if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        shouldThrow<IllegalStateException> {
            getEnvVarAsList(envName)
        }
    }

    @Test
    fun `Function getEnvVarAsList should allow for different variable separators`() {
        val envVarWithSeparators = "one|two.three|four"

        every { SystemWrapper.getEnvVar(envName) } returns envVarWithSeparators

        val expectedDefault = listOf("one|two.three|four")
        val expectedPipe = listOf("one", "two.three", "four")
        val expectedDot = listOf("one|two", "three|four")

        getEnvVarAsList(envName) shouldBe expectedDefault
        getEnvVarAsList(envName, separator = "|") shouldBe expectedPipe
        getEnvVarAsList(envName, separator = ".") shouldBe expectedDot
    }

    @Test
    fun `Function getOptionalEnvVarAsList should return correct environment variable if it exists`() {
        val default = listOf("default")
        val expected = listOf("one", "two", "three")

        every { SystemWrapper.getEnvVar(envName) } returns listEnvVal


        val result = getOptionalEnvVarAsList(envName, default)

        result shouldBe expected
    }

    @Test
    fun `Function getOptionalEnvVarAsList should return default value if provided and variable was not found`() {
        val default = listOf("default")

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsList(envName, default)

        result shouldBe default
    }

    @Test
    fun `Function getOptionalEnvVarAsList should return empty list if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsList(envName)

        result shouldBe emptyList()
    }

    @Test
    fun `Function getOptionalEnvVarAsList should allow for different variable separators`() {
        val envVarWithSeparators = "one|two.three|four"

        every { SystemWrapper.getEnvVar(envName) } returns envVarWithSeparators

        val expectedDefault = listOf("one|two.three|four")
        val expectedPipe = listOf("one", "two.three", "four")
        val expectedDot = listOf("one|two", "three|four")

        getOptionalEnvVarAsList(envName) shouldBe expectedDefault
        getOptionalEnvVarAsList(envName, separator = "|") shouldBe expectedPipe
        getOptionalEnvVarAsList(envName, separator = ".") shouldBe expectedDot
    }
}
