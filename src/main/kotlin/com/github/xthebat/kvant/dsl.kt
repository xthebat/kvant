package com.github.xthebat.kvant

import com.github.xthebat.kvant.core.Engine
import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.interfaces.Component
import com.github.xthebat.kvant.systems.AbstractSystem

data class EngineEntity(val engine: Engine, val entity: Entity)

/**
 * Method creates, initialize using [initialize] and registers entity in this Engine and returns it.
 *
 * @param initialize lambda to configure/initialize creating entity
 * @return created entity initialized by [initialize] lambda
 */
inline fun Engine.entity(initialize: EngineEntity.() -> Unit) = factory.entity().also {
    initialize(EngineEntity(this, it))
    addEntity(it)
}

/**
 * Method registers system specified by [construct] in this Engine and returns the given system.
 * Useful for dsl-like description of an Engine.
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

/**
 * Method creates component specified by generic class [T] and registers it in current entity.
 * Also initialize component after construct using [initialize] lambda.
 * Should be used within entity extension-function for an Engine.
 *
 * @see entity
 * @param initialize lambda to initialize component
 * @return component constructed as [T] class
 */
inline fun <reified T: Component> EngineEntity.with(initialize: T.() -> Unit) =
    entity.component { engine.factory.component(T::class).apply(initialize) }