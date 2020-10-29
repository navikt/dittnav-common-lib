package no.nav.personbruker.dittnav.common.logging.util

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class LoggerKtTest {
    @Test
    fun `Extension value logger should be the same instance across different instances of the same class definition`() {
        class DummyClass

        val dummyClass1 = DummyClass()
        val dummyClass2 = DummyClass()

        dummyClass1.logger `should be` dummyClass2.logger
    }

    @Test
    fun `Extension value logger should be unique for each class definition`() {
        class DummyClassOne
        class DummyClassTwo

        val classOne = DummyClassOne()
        val classTwo = DummyClassTwo()

        classOne.logger `should not be` classTwo.logger
    }

    @Test
    fun `Extension logger name should be the same as that of the conventional initialization`() {
        class DummyClass {
            val convention = LoggerFactory.getLogger(DummyClass::class.java)
        }

        val dummyClass = DummyClass()

        dummyClass.logger.name `should be equal to` dummyClass.convention.name
    }
}