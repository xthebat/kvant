package com.github.xthebat.kvant.systems

import com.github.xthebat.kvant.ComponentClass
import com.github.xthebat.kvant.core.Entity

/**
 * Base class for entities oriented systems.
 * This kind of system iterate over entities received from an Engine with specified classes.
 *
 * @param types array of classes to get from an Engine before call process for them
 * @param priority see [AbstractSystem.priority]
 *
 * @since v0.1
 */
abstract class IteratingSystem(vararg types: ComponentClass, priority: Int = 0) : AbstractSystem(priority) {
    private val entities by lazy { engine.entitiesOf(*types) }

    override fun update(delta: Float) = entities.forEach { process(it, delta) }

    /**
     * Method should defines what to do with any entity that fits specified types in constructor
     *
     * @param entity is entity to process
     * @param delta see [AbstractSystem.update]
     */
    abstract fun process(entity: Entity, delta: Float)
}