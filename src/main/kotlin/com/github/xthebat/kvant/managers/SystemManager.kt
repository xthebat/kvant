package com.github.xthebat.kvant.managers

import com.github.xthebat.kvant.ObjectCallback
import com.github.xthebat.kvant.SystemClass
import com.github.xthebat.kvant.systems.AbstractSystem
import ru.inforion.lab403.common.logging.INFO
import ru.inforion.lab403.common.logging.logger
import kotlin.reflect.KClass

internal class SystemManager : Iterable<AbstractSystem> {
    companion object {
        val log = logger(INFO)

        inline fun create(initialize: SystemManager.() -> Unit) = SystemManager().apply(initialize)
    }

    private val systems = mutableListOf<AbstractSystem>()
    private val mapper = mutableMapOf<SystemClass, AbstractSystem>()

    private var addedNotifier: ObjectCallback<AbstractSystem>? = null
    private var removedNotifier: ObjectCallback<AbstractSystem>? = null

    fun add(system: AbstractSystem) {
        val oldSystem = this[system::class]

        if (oldSystem != null) {
            log.fine { "System with type $oldSystem already in engine ... removing" }
            remove(oldSystem)
        }

        check(systems.add(system)) { "System was not added to engine but should be..." }

        mapper[system::class] = system
        systems.sortByDescending { it.priority }
        addedNotifier?.invoke(system)

        log.fine { "System $system added to engine" }
    }

    fun remove(system: AbstractSystem) {
        if (systems.remove(system)) {
            mapper.remove(system::class)
            removedNotifier?.invoke(system)
            log.fine { "System $system removed from engine" }
        } else {
            log.fine { "System $system not found in engine" }
        }
    }

    operator fun <T: AbstractSystem> get(type: KClass<out T>) = mapper[type]

    override fun iterator() = systems.iterator()

    fun notifyAdded(notifier: ObjectCallback<AbstractSystem>) = run { addedNotifier = notifier}

    fun notifyRemoved(notifier: ObjectCallback<AbstractSystem>) = run { removedNotifier = notifier}
}