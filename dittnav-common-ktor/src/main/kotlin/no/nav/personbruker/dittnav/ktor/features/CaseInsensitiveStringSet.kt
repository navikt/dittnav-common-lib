package no.nav.personbruker.dittnav.ktor.features

internal class CaseInsensitiveStringSet: MutableSet<String> {

    private val backingSet = HashSet<CaseInsensitiveString>()

    override fun add(element: String) = backingSet.add(CaseInsensitiveString(element))


    override fun addAll(elements: Collection<String>) = elements
        .map { CaseInsensitiveString(it) }
        .let { backingSet.addAll(it) }

    override fun clear() = backingSet.clear()

    override fun iterator() = backingSet
        .map { it.value }
        .toMutableSet()
        .iterator()

    override fun remove(element: String) = backingSet.remove(CaseInsensitiveString(element))

    override fun removeAll(elements: Collection<String>) = elements
        .map { CaseInsensitiveString(it) }
        .let { backingSet.removeAll(it) }

    override fun retainAll(elements: Collection<String>) = elements
        .map { CaseInsensitiveString(it) }
        .let { backingSet.retainAll(it) }

    override val size: Int
        get() = backingSet.size

    override fun contains(element: String) = backingSet.contains(CaseInsensitiveString(element))

    override fun containsAll(elements: Collection<String>) = elements
        .map { CaseInsensitiveString(it) }
        .let { backingSet.containsAll(it) }

    override fun isEmpty() = backingSet.isEmpty()
}

private class CaseInsensitiveString(val value: String) {
    val cachedHash = value.toLowerCase().hashCode()

    override fun equals(other: Any?): Boolean {
        return other is CaseInsensitiveString
                && other.value.equals(value, true)
    }

    override fun hashCode(): Int = cachedHash

    override fun toString() = value
}