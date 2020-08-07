package no.nav.personbruker.dittnav.common.util.kafka.exception

import no.nav.personbruker.dittnav.common.util.exception.ExceptionWithContext

class RetriableKafkaException(message: String, cause: Throwable?) : ExceptionWithContext(message, cause) {
    constructor(message: String) : this(message, null)
}