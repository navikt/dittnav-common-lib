package no.nav.personbruker.dittnav.common.util.config

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getEnvVarAsTypedList
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsType
import no.nav.personbruker.dittnav.common.util.config.TypedEnvVar.getOptionalEnvVarAsTypedList
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

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

        val result = getEnvVarAsType(envName, String::toInt, default)

        result `should be equal to` envVal
    }

    @Test
    fun `Function getEnvVarAsType should return default value if provided and variable was not found`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getEnvVarAsType(envName, String::toInt, default)

        result `should be equal to` default
    }

    @Test
    fun `Function getEnvVarAsType should throw exception if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        invoking { getEnvVarAsType(envName, String::toInt) } `should throw` IllegalStateException::class
    }

    @Test
    fun `Function getEnvVarAsType should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "onetwothree"

        invoking { getEnvVarAsType(envName, String::toInt) } `should throw` IllegalArgumentException::class
    }

    @Test
    fun `Function getOptionalEnvVarAsType should return correct environment variable if it exists`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns rawEnvVal

        val result = getOptionalEnvVarAsType(envName, String::toInt, default)

        result `should be equal to` envVal
    }

    @Test
    fun `Function getOptionalEnvVarAsType should return default value if provided and variable was not found`() {
        val default = 456

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsType(envName, String::toInt, default)

        result `should be equal to` default
    }

    @Test
    fun `Function getOptionalEnvVarAsType should return null if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsType(envName, String::toInt)

        result `should be equal to` null
    }

    @Test
    fun `Function getOptionalEnvVarAsType should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "onetwo"

        invoking { getEnvVarAsType(envName, String::toInt) } `should throw` IllegalArgumentException::class
    }

    @Test
    fun `Function getEnvVarAsTypedList should return correct environment variable if it exists`() {
        val default = listOf(1)

        every { SystemWrapper.getEnvVar(envName) } returns rawListEnvVal

        val result = getEnvVarAsTypedList(envName, String::toInt, default)

        result `should be equal to` listEnvVal
    }

    @Test
    fun `Function getEnvVarAsTypedList should return default value if provided and variable was not found`() {
        val default = listOf(1)

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getEnvVarAsTypedList(envName, String::toInt, default)

        result `should be equal to` default
    }

    @Test
    fun `Function getEnvVarAsTypedList should throw exception if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        invoking { getEnvVarAsTypedList(envName, String::toInt) } `should throw` IllegalStateException::class
    }

    @Test
    fun `Function getEnvVarAsTypedList should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "one,two"

        invoking { getEnvVarAsTypedList(envName, String::toInt) } `should throw` IllegalArgumentException::class
    }

    @Test
    fun `Function getEnvVarAsTypedList should allow for different variable separators`() {
        val envVarWithSeparators = "4|5|6"

        every { SystemWrapper.getEnvVar(envName) } returns envVarWithSeparators

        val expected = listOf(4, 5, 6)

        getEnvVarAsTypedList(envName, String::toInt, separator = "|") `should be equal to` expected
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should return correct environment variable if it exists`() {
        val default = listOf(1)

        every { SystemWrapper.getEnvVar(envName) } returns rawListEnvVal

        val result = getOptionalEnvVarAsTypedList(envName, String::toInt, default)

        result `should be equal to` listEnvVal
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should return default value if provided and variable was not found`() {
        val default = listOf(4, 5)

        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsTypedList(envName, String::toInt, default)

        result `should be equal to` default
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should return empty list if variable was not found and no default was specified`() {
        every { SystemWrapper.getEnvVar(envName) } returns null

        val result = getOptionalEnvVarAsTypedList(envName, String::toInt)

        result `should be equal to` emptyList()
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should throw exception if variable could not be mapped`() {
        every { SystemWrapper.getEnvVar(envName) } returns "one,two"

        invoking { getOptionalEnvVarAsTypedList(envName, String::toInt) } `should throw` IllegalArgumentException::class
    }

    @Test
    fun `Function getOptionalEnvVarAsTypedList should allow for different variable separators`() {
        val envVarWithSeparators = "7|8|9"

        every { SystemWrapper.getEnvVar(envName) } returns envVarWithSeparators

        val expected = listOf(7, 8, 9)

        getOptionalEnvVarAsTypedList(envName, String::toInt, separator = "|") `should be equal to` expected
    }
}