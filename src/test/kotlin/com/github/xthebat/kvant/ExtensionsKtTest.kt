package com.github.xthebat.kvant

import org.junit.Test
import kotlin.test.assertTrue

internal class ExtensionsKtTest {
    @Test
    fun listWhileNotEmptyTest() {
        val collection = mutableListOf(1, 2, 3)
        collection.whileNotEmpty { collection.remove(it) }
        assertTrue { collection.isEmpty() }
    }

    @Test
    fun mapWhileNotEmptyTest() {
        val collection = mutableMapOf("1" to 1, "2" to 2)
        collection.whileNotEmpty { collection.remove(it) }
        assertTrue { collection.isEmpty() }
    }
}