package no.nav.personbruker.dittnav.common.util.database.exception

class BatchUpdateException(message: String, cause: Throwable?) : RetriableDatabaseException(message, cause) {
    constructor(message: String) : this(message, null)
}
