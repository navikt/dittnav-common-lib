package no.nav.personbruker.dittnav.common.util.list

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class ListPartitioningKtTest {

    @Test
    fun `Function partitionToIndex should create a sublist starting at index 0 up to and excluding parameter`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.partitionToIndex(3)

        val expected = listOf(1, 2, 3)

        partition `should be equal to` expected
    }

    @Test
    fun `Function mutablePartitionToIndex should create a mutable sublist starting at index 0 up to and excluding parameter`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.mutablePartitionToIndex(3)

        val expected = listOf(1, 2, 3)

        partition `should be equal to` expected

        partition.add(4)

        partition `should be equal to` expected + 4
    }

    @Test
    fun `Function partitionFromIndex should create a sublist starting at and including parameter, up to and including last entry`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.partitionFromIndex(3)

        val expected = listOf(4, 5)

        partition `should be equal to` expected
    }

    @Test
    fun `Function mutablePartitionToIndex should create a mutable sublist starting at and including parameter, up to and including last entry`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.mutablePartitionFromIndex(3)

        val expected = listOf(4, 5)

        partition `should be equal to` expected

        partition.add(6)

        partition `should be equal to` expected + 6
    }

    @Test
    fun `Changing mutable partitions should not affect original list`() {
        val original = listOf(1, 2, 3, 4, 5)
        val numbers = listOf(1, 2, 3, 4, 5)

        val partitionTo = numbers.mutablePartitionToIndex(2)
        partitionTo[0] = 10

        partitionTo `should be equal to` listOf(10, 2)
        numbers `should be equal to` original

        val partitionFrom = numbers.mutablePartitionFromIndex(2)
        partitionFrom[0] = 10

        partitionFrom `should be equal to` listOf(10, 4, 5)
        numbers `should be equal to` original
    }
}