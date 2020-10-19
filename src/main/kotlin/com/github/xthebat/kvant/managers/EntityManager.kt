package com.github.xthebat.kvant.managers

import com.github.xthebat.kvant.ComponentClass
import com.github.xthebat.kvant.ObjectCallback
import com.github.xthebat.kvant.common.FilteredIterator
import com.github.xthebat.kvant.whileNotEmpty
import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.managers.EntityManager.Operation
import ru.inforion.lab403.common.logging.INFO
import ru.inforion.lab403.common.logging.logger

internal class EntityManager : Iterable<Entity> {
    companion object {
        val log = logger(INFO)

        inline fun create(initialize: EntityManager.() -> Unit) = EntityManager().apply(initialize)
    }

    private fun interface Operation {
        fun execute()
    }

    // Performance optimization
    private val entitySet = mutableSetOf<Entity>()
    private val scheduledRemoving = mutableSetOf<Entity>()
    private val pendingOperations = mutableListOf<Operation>()
    private val entities = mutableListOf<Entity>()

    private var addedNotifier: ObjectCallback<Entity>? = null
    private var removedNotifier: ObjectCallback<Entity>? = null

    private fun addEntityInternal(entity: Entity) {
        require(entitySet.add(entity)) { "Entity is already registered $entity" }
        entities.add(entity)
        addedNotifier?.invoke(entity)
    }

    private fun removeEntityInternal(entity: Entity) {
        if (entitySet.remove(entity)) {
            scheduledRemoving.remove(entity)
            entities.remove(entity)
            removedNotifier?.invoke(entity)
        }
    }

    private fun removeAllEntitiesInternal() = entities.whileNotEmpty { removeEntityInternal(it) }

    fun add(entity: Entity, delayed: Boolean = false) {
        if (delayed) {
            log.fine { "Queue add to engine entity $entity" }
            pendingOperations.add(Operation { addEntityInternal(entity) })
        } else {
            log.fine { "Add to engine entity $entity" }
            addEntityInternal(entity)
        }
    }

    fun remove(entity: Entity, delayed: Boolean = false) {
        if (delayed) {
            if (entity in scheduledRemoving) return
            log.fine { "Queue remove from engine entity $entity" }
            scheduledRemoving.add(entity)
            pendingOperations.add(Operation { removeEntityInternal(entity) })
        } else {
            log.fine { "Remove from engine entity $entity" }
            removeEntityInternal(entity)
        }
    }

    fun removeAll(delayed: Boolean = false) {
        if (delayed) {
            entities.forEach { scheduledRemoving.add(it) }
            pendingOperations.add(Operation { removeAllEntitiesInternal() })
        } else {
            removeAllEntitiesInternal()
        }
    }

    fun processPendingOperations() = pendingOperations.removeAll { it.execute(); true }

    fun entitiesOf(vararg types: ComponentClass) = FilteredIterator(entities) { entity ->
        // TODO: optimization
        types.any { type -> entity.any { component -> type.isInstance(component) } }
    }

    override fun iterator() = entities.iterator()

    fun notifyAdded(notifier: ObjectCallback<Entity>) = run { addedNotifier = notifier}

    fun notifyRemoved(notifier: ObjectCallback<Entity>) = run { removedNotifier = notifier}
}