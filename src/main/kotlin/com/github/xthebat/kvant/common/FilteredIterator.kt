package com.github.xthebat.kvant.common

/**
 * Class contains [collection] and returns iterator for filtered collection for specified [predicate].
 * Result of filtering cached in internal iterator state. So for next iteration cached collection will return.
 * To recalculate collection filter, [dirty] property should be set true.
 *
 * @property collection collection to filter
 * @property predicate is predicate to match items of collection
 *
 * @since v0.1
 */
class FilteredIterator<T> constructor(
        private val collection: Collection<T>,
        private val predicate: (T) -> Boolean
) : Iterable<T> {
    private lateinit var filtered: List<T>

    /**
     * If property is true then when [iterator] called [collection] filter will be recalculated.
     */
    var dirty = true

    override fun iterator(): Iterator<T> {
        if (dirty) {
            filtered = collection.filter(predicate)
            dirty = false
        }
        return filtered.iterator()
    }
}