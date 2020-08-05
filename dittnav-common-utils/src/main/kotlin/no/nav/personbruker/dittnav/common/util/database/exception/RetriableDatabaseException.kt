package no.nav.personbruker.dittnav.common.util.database.exception

open class RetriableDatabaseException(message: String, cause: Throwable?) : AbstractPersonbrukerException(message, cause) {
    constructor(message: String) : this(message, null)
}
