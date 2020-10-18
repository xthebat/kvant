package com.github.xthebat.kvant

import com.github.xthebat.kvant.core.Engine
import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.interfaces.Component
import com.github.xthebat.kvant.systems.AbstractSystem

inline fun <T>MutableCollection<T>.whileNotEmpty(action: (T) -> Unit) {
    while (size > 0) action(first())
}

inline fun <K, V>MutableMap<K, V>.whileNotEmpty(action: (K) -> Unit) {
    while (size > 0) action(keys.first())
}

/**
 * Method creates, initialize using [initialize] and registers entity in this Engine and returns it.
 *
 * @param initialize lambda to configure/initialize creating entity
 * @return created entity initialized by [initialize] lambda
 */
inline fun Engine.entity(flags: Int = 0, initialize: Entity.() -> Unit) = Entity(flags).also {
    initialize(it)
    addEntity(it)
}

/**
 * Method registers system specified by [construct] in this Engine and returns the given system
 *
 * @param construct lambda to create system for registration in the Engine
 * @return constructed by [construct] lambda system
 */
inline fun <T: AbstractSystem> Engine.system(construct: () -> T) = construct().also { addSystem(it) }

/**
 * Method registers component specified by [construct] in this entity and returns the given component.
 *
 * @param construct lambda to create component for registration in the entity
 * @return constructed by [construct] lambda component
 */
inline fun <T: Component> Entity.component(construct: () -> T) = construct().also {
    check(add(it)) { "Component with class ${it::class} already in entity $this" }
}