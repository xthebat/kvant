@file:Suppress("NOTHING_TO_INLINE")

package com.github.xthebat.kvant

import kotlin.reflect.KClass

inline fun <T>MutableCollection<T>.whileNotEmpty(action: (T) -> Unit) {
    while (size > 0) action(first())
}

inline fun <K, V>MutableMap<K, V>.whileNotEmpty(action: (K) -> Unit) {
    while (size > 0) action(keys.first())
}

inline fun <T: Any> KClass<out T>.newInstance(): T = this.java.getDeclaredConstructor().newInstance()