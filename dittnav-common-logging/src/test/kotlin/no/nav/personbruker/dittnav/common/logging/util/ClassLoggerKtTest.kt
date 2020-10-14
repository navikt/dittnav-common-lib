package no.nav.personbruker.dittnav.common.logging.util

import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class ClassLoggerKtTest {

    @Test
    fun `Should create a logger with expected logger name according to convention`() {
        class DummyClass {
            val convention = LoggerFactory.getLogger(DummyClass::class.java)
            val logger = createClassLogger()
        }

        val classInstance = DummyClass()

        classInstance.logger.name `should equal` classInstance.convention.name
    }
}