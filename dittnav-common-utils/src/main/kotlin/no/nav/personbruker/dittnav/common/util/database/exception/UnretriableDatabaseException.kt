package no.nav.personbruker.dittnav.common.util.database.exception

import no.nav.personbruker.dittnav.common.util.exception.ExceptionWithContext

class UnretriableDatabaseException(message: String, cause: Throwable?) : ExceptionWithContext(message, cause) {
    constructor(message: String) : this(message, null)
} 
