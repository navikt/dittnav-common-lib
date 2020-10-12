package no.nav.personbruker.dittnav.common.metrics.masking

import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ProducerNameScrubberTest {

    private val fallbackName = "defaultFallback"

    private val sysUserFallbackName = "sysUserFallback"
    private val user = "user"

    private val sysUser = "srvUser"
    private val alias = "Scrubbed Name"

    private val aliasResolver: PublicAliasResolver = mockk()

    private val producerNameScrubber = ProducerNameScrubber(aliasResolver, defaultUser = fallbackName, defaultSystemUser = sysUserFallbackName)

    @AfterEach
    fun cleanUp() {
        clearMocks(aliasResolver)
    }

    @Test
    fun `Should return correct mapping when alias was found`() {
        coEvery { aliasResolver.getProducerNameAlias(user) } returns alias

        val result = runBlocking {
            producerNameScrubber.getPublicAlias(user)
        }

        result `should equal` alias
    }

    @Test
    fun `Should return normal fallback mapping if no alias was found and name does not look like sys user name`() {
        coEvery { aliasResolver.getProducerNameAlias(user) } returns null

        val result = runBlocking {
            producerNameScrubber.getPublicAlias(user)
        }

        result `should equal` fallbackName
    }

    @Test
    fun `Should return sysUser fallback mapping if no alias was found and name looks like sys user name`() {
        coEvery { aliasResolver.getProducerNameAlias(sysUser) } returns null

        val result = runBlocking {
            producerNameScrubber.getPublicAlias(sysUser)
        }

        result `should equal` sysUserFallbackName
    }

    @Test
    fun `Should return normal fallback mapping if no alias was found and name is too long to be a sys user name`() {
        val name = "srvTooLongToBeASysUserName"

        coEvery { aliasResolver.getProducerNameAlias(name) } returns null

        val result = runBlocking {
            producerNameScrubber.getPublicAlias(name)
        }

        result `should equal` fallbackName
    }
}