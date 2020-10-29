package no.nav.personbruker.dittnav.common.metrics.masking

import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class PublicAliasResolverTest {

    private val provider: ProviderWrapper<NameAndPublicAlias> = mockk()

    private val name = "private name"
    private val alias = "public alias"

    private val aliases = listOf(NameAndPublicAlias(name, alias))

    @AfterEach
    fun cleanUp() {
        clearMocks(provider)
    }

    @Test
    fun `Should update cache upon cache miss and return correct alias`() {
        val resolver = PublicAliasResolver(provider::invoke)

        coEvery { provider.invoke() } returns aliases

        val result = runBlocking {
            resolver.getProducerNameAlias(name)
        }

        result `should be equal to` alias
        coVerify{ provider.invoke() }
    }

    @Test
    fun `Should not update aliases again upon cache hit`() {
        val resolver = PublicAliasResolver(provider::invoke)

        coEvery { provider.invoke() } returns aliases

        val result = runBlocking {
            resolver.getProducerNameAlias(name)
            resolver.getProducerNameAlias(name)
        }

        result `should be equal to` alias
        coVerify(exactly = 1){ provider.invoke() }
    }

    @Test
    fun `Should update cache upon every cache miss`() {
        val otherName = "otherName"
        val resolver = PublicAliasResolver(provider::invoke)

        coEvery { provider.invoke() } returns aliases

        val result = runBlocking {
            resolver.getProducerNameAlias(otherName)
            resolver.getProducerNameAlias(otherName)
        }

        result `should be equal to` null
        coVerify(exactly = 2){ provider.invoke() }
    }

    interface ProviderWrapper<V> {
        suspend operator fun invoke(): List<V>
    }
}