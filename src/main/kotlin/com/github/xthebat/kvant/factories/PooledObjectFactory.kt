package com.github.xthebat.kvant.factories

import com.github.xthebat.kvant.ComponentClass
import com.github.xthebat.kvant.common.Pool
import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.interfaces.Component
import com.github.xthebat.kvant.newInstance
import kotlin.reflect.KClass

class PooledObjectFactory : ObjectFactory {
    private val entities = Pool<EntityPoolableAdapter>()

    private val components = mutableMapOf<ComponentClass, Pool<Component>>()

    private class EntityPoolableAdapter(val entity: Entity) : Pool.Poolable {
        override fun reset() = with(entity) {
            removeAll()
            notifyAdded(null)
            notifyRemoved(null)
            flags = 0
        }
    }

    override fun entity() = entities.obtain { EntityPoolableAdapter(Entity()) }.entity

    override fun <T: Component> component(type: KClass<out T>): T {
        val pool = components.getOrPut(type) { Pool() }
        return pool.obtain { type.newInstance() } as T
    }

    override fun clearCache() {
        entities.clear()
        components.values.forEach { it.clear() }
    }

    override fun freeEntity(entity: Entity) = entities.free(EntityPoolableAdapter(entity))

    override fun freeComponent(component: Component) = components.getOrPut(component::class) { Pool() }.free(component)
}