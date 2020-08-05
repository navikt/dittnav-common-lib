package no.nav.personbruker.dittnav.common.util.database.exception

class UnretriableDatabaseException(message: String, cause: Throwable?) : AbstractPersonbrukerException(message, cause) {
    constructor(message: String) : this(message, null)
} 
