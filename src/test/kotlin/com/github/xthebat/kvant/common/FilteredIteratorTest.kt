package com.github.xthebat.kvant.common

import org.junit.Test
import kotlin.test.expect


internal class FilteredIteratorTest {
    @Test
    fun dirtyTest() {
        val collection = mutableListOf(1, 2, 3, 4, 5, 6)

        val beforeDirty = listOf("1", "2")
        val afterDirty = listOf("1", "2", "0")

        val iterator = FilteredIterator(collection) { it < 3 }

        expect(beforeDirty) { iterator.map { "$it" } }
        collection.add(0)
        expect(beforeDirty) { iterator.map { "$it" } }

        iterator.dirty = true

        expect(afterDirty) { iterator.map { "$it" } }
    }
}