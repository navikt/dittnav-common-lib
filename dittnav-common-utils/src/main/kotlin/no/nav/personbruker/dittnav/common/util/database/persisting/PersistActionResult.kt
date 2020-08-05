package no.nav.personbruker.dittnav.eventaggregator.common.database

import no.nav.personbruker.dittnav.common.util.database.persisting.PersistFailureReason

class PersistActionResult private constructor(
        val entityId: Int, val wasSuccessful: Boolean, val persistOutcome: PersistFailureReason) {

    inline fun onSuccess(action: (Int) -> Unit): PersistActionResult {
        if (wasSuccessful) {
            action(entityId)
        }
        return this
    }

    inline fun onFailure(action: (PersistFailureReason) -> Unit): PersistActionResult {
        if (!wasSuccessful) {
            action(persistOutcome)
        }
        return this
    }

    companion object {
        fun success(entityId: Int): PersistActionResult =
                PersistActionResult(entityId, true, PersistFailureReason.NO_ERROR)

        fun failure(reason: PersistFailureReason): PersistActionResult =
                PersistActionResult(-1, false, reason)
    }
}


