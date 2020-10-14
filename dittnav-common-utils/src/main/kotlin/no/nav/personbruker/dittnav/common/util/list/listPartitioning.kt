package no.nav.personbruker.dittnav.common.util.list

fun <T> List<T>.partitionToIndex(indexExclusive: Int): List<T> {
    return when {
        indexExclusive > size -> toList()
        indexExclusive < 1 -> emptyList()
        else -> subList(0, indexExclusive).toList()
    }
}

fun <T> List<T>.mutablePartitionToIndex(indexExclusive: Int): MutableList<T> {
    return when {
        indexExclusive > size -> toMutableList()
        indexExclusive < 1 -> mutableListOf()
        else -> subList(0, indexExclusive).toMutableList()
    }
}

fun <T> List<T>.partitionFromIndex(indexInclusive: Int): List<T> {
    return when {
        indexInclusive < 1 -> toList()
        indexInclusive >= size -> emptyList()
        else -> subList(indexInclusive, size).toList()
    }
}

fun <T> List<T>.mutablePartitionFromIndex(indexInclusive: Int): MutableList<T> {
    return when {
        indexInclusive < 1 -> toMutableList()
        indexInclusive >= size -> mutableListOf()
        else -> subList(indexInclusive, size).toMutableList()
    }
}