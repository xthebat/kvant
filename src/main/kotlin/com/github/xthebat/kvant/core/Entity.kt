package com.github.xthebat.kvant.core

import com.github.xthebat.kvant.ComponentClass
import com.github.xthebat.kvant.ObjectCallback
import com.github.xthebat.kvant.whileNotEmpty
import com.github.xthebat.kvant.interfaces.Component
import kotlin.reflect.KClass

/**
 * Basic engine entity. It contains components that can be used in different systems.
 *
 * @property flags user specific field
 * @see [Engine]
 * @since v0.1
 */
class Entity constructor(var flags: Int = 0) : Iterable<Component> {
    companion object {
        inline fun create(initialize: Entity.() -> Unit) = Entity().apply(initialize)
    }

    private val components = mutableMapOf<ComponentClass, Component>()

    private var addedNotifier: ObjectCallback<Component>? = null
    private var removedNotifier: ObjectCallback<Component>? = null

    private fun addInternal(component: Component): Boolean {
        val type = component::class
        val oldComponent = get(type)
        if (component == oldComponent) return false
        if (oldComponent != null) removeInternal(type)
        components[type] = component
        return true
    }

    private fun removeInternal(type: ComponentClass) = components.remove(type)

    /**
     * Method adds new component to current entity.
     * If component was successfully added calls [addedNotifier] callback and returns true.
     *
     * @see [notifyAdded], [extensions]
     * @param component is component to add
     * @return true if component was added to entity
     */
    fun add(component: Component): Boolean {
        val success = addInternal(component)
        if (success) addedNotifier?.invoke(component)
        return success
    }

    /**
     * Method removes component by it class from current entity.
     * If component was actually removed calls [removedNotifier] callback and returns removed component.
     *
     * @see [notifyRemoved], [ComponentClass]
     * @param type is class of component to remove
     * @return component if it was actually removed
     */
    fun remove(type: ComponentClass) = removeInternal(type)?.also { removedNotifier?.invoke(it) }

    /**
     * Method removes all components from current entity.
     * If any component was actually removed calls [removedNotifier] callback
     *
     * @see [notifyRemoved], [ComponentClass] and [remove]
     */
    fun removeAll() = components.whileNotEmpty { remove(it) }

    /**
     * Returns total components count in this entity
     */
    val size get() = components.size

    /**
     * Returns component by concrete component class [type]
     *   and cast it to this class or null if component was not found.
     *
     * @param type concrete component class
     * @return component with specified type or null
     */
    operator fun <T: Component> get(type: KClass<out T>) = components[type] as? T

    /**
     * Checks if entity contains component with specified class [type].
     *
     * @return true if entity has component with specified class
     */
    operator fun contains(type: ComponentClass) = type in components

    /**
     * Iterates through all components in entity. Useful for Kotlin stdlib extension method for [Iterable]
     */
    override fun iterator() = components.values.iterator()

    /**
     * Registers [notifier] callback to call when component added to entity. Only one notifier supported.
     *
     * @param notifier callback to call when component added to entity
     */
    fun notifyAdded(notifier: ObjectCallback<Component>?) = run {
        addedNotifier = notifier
    }

    /**
     * Registers [notifier] callback to call when component removed from entity. Only one notifier supported.
     *
     * @param notifier callback to call when component removed from entity
     */
    fun notifyRemoved(notifier: ObjectCallback<Component>?) = run { removedNotifier = notifier}
}