package no.nav.personbruker.dittnav.common.util.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.IllegalArgumentException
import kotlin.IllegalStateException

internal class EnvVarAsTypeKtTest {

    private val envName = "PROPERTY_NAME"

    private val rawEnvVal = "123"
    private val envVal = 123
    private val rawListEnvVal = "1,2,3"
    private val listEnvVal = listOf(1, 2, 3)

    @BeforeEach
    fun setupMock() {
        mockkObject(SystemWrapper)
    }

    @AfterEach
    fun cleanUp() {
        unmockkObject(SystemWrapper)
    }

    @Test
    fun `Function getEnvVarAsType should map and return correct environment variable if it exists`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns rawEnvVal

        val result = getEnvVarAsType(envName, default, String::toInt)

        result shouldBe envVal
    }

    @Test
    fun `Function getEnvVarAsType should return default value if provided and variable was not found`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getEnvVarAsType(envName, default, String::toInt)

        result shouldBe default
    }

    @Test
    fun `Function getEnvVarAsType should throw exception if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        shouldThrow<IllegalStateException> {
            getEnvVarAsType(envName, mapper = String::toInt)
        }
    }

    @Test
    fun `Function getEnvVarAsType should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "onetwothree"

        shouldThrow<IllegalArgumentException> {
            getEnvVarAsType(envName, mapper = String::toInt)
        }
    }

    @Test
    fun `Function getOptionalEnvVarAsType should return correct environment variable if it exists`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns rawEnvVal

        val result = getOptionalEnvVarAsType(envName, default, String::toInt)

        result shouldBe envVal
    }

    @Test
    fun `Function getOptionalEnvVarAsType should return default value if provided and variable was not found`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsType(envName, default, String::toInt)

        result shouldBe default
    }

    @Test
    fun `Function getOptionalEnvVarAsType should return null if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsType(envName, mapper = String::toInt)

        result shouldBe null
    }

    @Test
    fun `Function getOptionalEnvVarAsType should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "onetwo"

        shouldThrow<IllegalArgumentException> { getEnvVarAsType(envName, mapper = String::toInt) }
    }

    @Test
    fun `Function getEnvVarAsTypedList should return correct environment variable if it exists`() {
        val default = listOf(1)

        every { SystemWrapper.getEnvVar(envName) } returns rawListEnvVal

        val result = getEnvVarAsTypedList(envName, default, mapper = String::toInt)

        result shouldBe listEnvVal
    }

    @Test
    fun `Function getEnvVarAsTypedList should return default value if provided and variable was not found`() {
        val default = listOf(1)

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getEnvVarAsTypedList(envName, default, mapper = String::toInt)

        result shouldBe default
    }

    @Test
    fun `Function getEnvVarAsTypedList should throw exception if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        shouldThrow<IllegalStateException> {
            getEnvVarAsTypedList(envName, mapper = String::toInt)
        }
    }

    @Test
    fun `Function getEnvVarAsTypedList should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "one,two"

        shouldThrow<IllegalArgumentException> {
            getEnvVarAsTypedList(envName, mapper = String::toInt)
        }
    }

    @Test
    fun `Function getEnvVarAsTypedList should allow for different variable separators`() {
        val envVarWithSeparators = "4|5|6"

        every { SystemWrapper.getEnvVar(envName) } returns envVarWithSeparators

        val expected = listOf(4, 5, 6)

        getEnvVarAsTypedList(envName, separator = "|", mapper = String::toInt) shouldBe expected
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should return correct environment variable if it exists`() {
        val default = listOf(1)

        every { SystemWrapper.getEnvVar(envName) } returns rawListEnvVal

        val result = getOptionalEnvVarAsTypedList(envName, default, mapper = String::toInt)

        result shouldBe listEnvVal
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should return default value if provided and variable was not found`() {
        val default = listOf(4, 5)

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsTypedList(envName, default, mapper = String::toInt)

        result shouldBe default
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should return empty list if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsTypedList(envName, mapper = String::toInt)

        result shouldBe emptyList()
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "one,two"

        shouldThrow<IllegalArgumentException> {
            getOptionalEnvVarAsTypedList(envName, mapper = String::toInt)
        }
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should allow for different variable separators`() {
        val envVarWithSeparators = "7|8|9"

        every { SystemWrapper.getEnvVar(envName) } returns envVarWithSeparators

        val expected = listOf(7, 8, 9)

        getOptionalEnvVarAsTypedList(envName, separator = "|", mapper = String::toInt) shouldBe expected
    }
}
