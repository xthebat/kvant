package com.github.xthebat.kvant.core

import com.github.xthebat.kvant.ComponentClass
import com.github.xthebat.kvant.managers.EntityManager
import com.github.xthebat.kvant.common.FilteredIterator
import com.github.xthebat.kvant.factories.ObjectFactory
import com.github.xthebat.kvant.factories.PooledObjectFactory
import com.github.xthebat.kvant.factories.BasicObjectFactory
import com.github.xthebat.kvant.managers.SystemManager
import com.github.xthebat.kvant.systems.AbstractSystem

/**
 * Base class for entity-component system. Contains all created entities.
 * Method [update] should be called at every render step.
 *
 * @since v0.1
 */
class Engine(val factory: ObjectFactory) {
    companion object {
        inline fun basic(initialize: Engine.() -> Unit) = Engine(BasicObjectFactory()).apply(initialize)

        inline fun pooled(initialize: Engine.() -> Unit) = Engine(PooledObjectFactory()).apply(initialize)
    }

    private var updating = false

    private val iterators = mutableListOf<FilteredIterator<Entity>>()

    private val systems = SystemManager.create {
        notifyAdded { it.addedToEngine(this@Engine) }
        notifyRemoved { it.removedFromEngine() }
    }

    private val entities = EntityManager.create {
        // Call dirty to all filtered iterators to update it internal lists for systems.
        // It should be called in notifier because of possible postponed operations.
        notifyAdded { dirtyIterators() }

        notifyRemoved { entity ->
            // TODO: Likely lock must be here
            // Don't change order
            entity.forEach { factory.freeComponent(it) }
            factory.freeEntity(entity)
            dirtyIterators()
        }
    }

    private fun dirtyIterators() = iterators.forEach { it.dirty = true }

    /**
     * Method adds entity to this Engine using [entities] manager
     *
     * @see [EntityManager], [FilteredIterator], [extensions]
     * @param entity is entity to add to this Engine
     */
    fun addEntity(entity: Entity) = entities.add(entity, updating)

    /**
     * Method removes entity from this Engine using [entities] manager
     *
     * @see [EntityManager], [FilteredIterator]
     * @param entity is entity to remove to this Engine
     */
    fun removeEntity(entity: Entity) = entities.remove(entity, updating)

    /**
     * Method removes all entities from this Engine using [entities] manager
     *
     * @see [EntityManager], [FilteredIterator]
     */
    fun removeAllEntities() = entities.removeAll(updating)

    /**
     * Method adds specified [system] to this Engine and also run [AbstractSystem.addedToEngine] method
     *
     * @see [AbstractSystem.addedToEngine], [extensions]
     * @param system is system to add to this Engine
     */
    fun addSystem(system: AbstractSystem) = systems.add(system)

    /**
     * Method adds specified system [newSystems] to this Engine.
     * It's just wrapper for [addSystem] method.
     *
     * @param newSystems array of systems to add this Engine
     */
    fun addSystems(vararg newSystems: AbstractSystem) = newSystems.forEach { addSystem(it) }

    /**
     * Method removes specified [system] from this Engine
     *
     * @param system is system to remove
     */
    fun removeSystem(system: AbstractSystem) = systems.remove(system)

    /**
     * The main method of an Engine that should be called at each render frame.
     * Method iterates through all system registered in this Engine and invoke [AbstractSystem.update]
     *   if system is enabled. Also process pending entities operations.
     *
     * @param delta the time in seconds since the last update
     */
    fun update(delta: Float) {
        check(!updating) { "Cannot call update() on an Engine that is already updating" }

        // force to process pending operations even if all system disabled now
        entities.processPendingOperations()

        try {
            systems.filter { it.enabled }.forEach {
                it.update(delta)
                entities.processPendingOperations()
            }
        } finally {
            updating = false
        }
    }

    /**
     * Returns special iterator [FilteredIterator] that prefilter only entities with specified classes [types].
     *
     * @see [FilteredIterator], [EntityManager]
     * @param types classes to filter engine entities
     * @return iterator of class [FilteredIterator] contains only entities of specified classes
     */
    fun entitiesOf(vararg types: ComponentClass) = entities.entitiesOf(*types).also { iterators.add(it) }
}