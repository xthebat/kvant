package com.github.xthebat.kvant.common

import java.util.*

class Pool<T>(val max: Int = Int.MAX_VALUE) {

    fun interface Poolable {
        fun reset()
    }

    private val freeObjects = LinkedList<T>()

    fun obtain(constructor: () -> T) = if (freeObjects.isEmpty()) constructor() else freeObjects.removeAt(0)

    fun free(obj: T) {
        if (freeObjects.size < max)
            freeObjects.add(obj)
        if (obj is Poolable) obj.reset()
    }

    fun freeAll(objects: Collection<T>) = objects.forEach { if (it != null) free(it) }

    fun fill(size: Int, constructor: () -> T) {
        repeat(size) { if (freeObjects.size < max) freeObjects.add(constructor()) }
    }

    val free get() = freeObjects.size

    fun clear() = freeObjects.clear()
}