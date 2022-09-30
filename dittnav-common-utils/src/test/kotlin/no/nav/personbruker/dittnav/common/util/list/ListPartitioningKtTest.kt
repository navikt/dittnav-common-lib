package no.nav.personbruker.dittnav.common.util.list

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class ListPartitioningKtTest {

    @Test
    fun `Function partitionToIndex should create a sublist starting at index 0 up to and excluding parameter`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.partitionToIndex(3)

        val expected = listOf(1, 2, 3)

        partition shouldBe expected
    }

    @Test
    fun `Function mutablePartitionToIndex should create a mutable sublist starting at index 0 up to and excluding parameter`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.mutablePartitionToIndex(3)

        val expected = listOf(1, 2, 3)

        partition shouldBe expected

        partition.add(4)

        partition shouldBe expected + 4
    }

    @Test
    fun `Function partitionFromIndex should create a sublist starting at and including parameter, up to and including last entry`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.partitionFromIndex(3)

        val expected = listOf(4, 5)

        partition shouldBe expected
    }

    @Test
    fun `Function mutablePartitionToIndex should create a mutable sublist starting at and including parameter, up to and including last entry`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val partition = numbers.mutablePartitionFromIndex(3)

        val expected = listOf(4, 5)

        partition shouldBe expected

        partition.add(6)

        partition shouldBe expected + 6
    }

    @Test
    fun `Changing mutable partitions should not affect original list`() {
        val original = listOf(1, 2, 3, 4, 5)
        val numbers = listOf(1, 2, 3, 4, 5)

        val partitionTo = numbers.mutablePartitionToIndex(2)
        partitionTo[0] = 10

        partitionTo shouldBe listOf(10, 2)
        numbers shouldBe original

        val partitionFrom = numbers.mutablePartitionFromIndex(2)
        partitionFrom[0] = 10

        partitionFrom shouldBe listOf(10, 4, 5)
        numbers shouldBe original
    }
}
