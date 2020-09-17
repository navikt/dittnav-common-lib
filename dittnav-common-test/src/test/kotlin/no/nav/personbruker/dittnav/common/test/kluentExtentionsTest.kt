package no.nav.personbruker.dittnav.common.test

import org.amshove.kluent.invoking
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test

internal class kluentExtentionsTest {

    @Test
    fun `Skal matche paa sub-string av exception-melding`() {
        invoking {
            throw Exception("Simulert feil")

        } shouldThrow Exception::class `with message containing` "Simulert"
    }

}
