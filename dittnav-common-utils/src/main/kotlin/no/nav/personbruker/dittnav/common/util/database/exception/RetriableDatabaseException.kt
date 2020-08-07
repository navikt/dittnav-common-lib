package no.nav.personbruker.dittnav.common.util.database.exception

import no.nav.personbruker.dittnav.common.util.exception.ExceptionWithContext

open class RetriableDatabaseException(message: String, cause: Throwable?) : ExceptionWithContext(message, cause) {
    constructor(message: String) : this(message, null)
}
