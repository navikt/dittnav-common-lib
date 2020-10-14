package no.nav.personbruker.dittnav.common.util.database.fetching

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.sql.ResultSet

internal object ResultSetMock {

    fun createResultSetMock(results: List<Map<String, String>>): ResultSet {
        var index = -1

        val mock: ResultSet = mockk()

        every { mock.next() } answers {
            if (index + 1 < results.size) {
                index++
                true
            } else {
                false
            }
        }

        val labelCaptor = slot<String>()

        every { mock.getString(capture(labelCaptor)) } answers {
            results[index][labelCaptor.captured]
        }
        every { mock.getInt(capture(labelCaptor)) } answers {
            results[index][labelCaptor.captured]!!.toInt()
        }
        every { mock.getLong(capture(labelCaptor)) } answers {
            results[index][labelCaptor.captured]!!.toLong()
        }

        return mock
    }
}