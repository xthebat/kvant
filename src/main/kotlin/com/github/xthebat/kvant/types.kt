package com.github.xthebat.kvant

import com.github.xthebat.kvant.interfaces.Component
import com.github.xthebat.kvant.systems.AbstractSystem
import kotlin.reflect.KClass

typealias ObjectCallback<T> = (T) -> Unit

internal typealias ComponentClass = KClass<out Component>

internal typealias SystemClass = KClass<out AbstractSystem>