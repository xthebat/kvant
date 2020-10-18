package com.github.xthebat.kvant.systems

import com.github.xthebat.kvant.core.Engine

/**
 * Basic abstract class of engine systems
 *
 * @property priority system priority. Higher priority system will process first.
 * @property enabled is system currently enabled. Disabled system will not be processed.
 *
 * @since v0.1
 */
abstract class AbstractSystem constructor(val priority: Int = 0, var enabled: Boolean = true) {
    private var engineIntern: Engine? = null

    /**
     * Property returns currently setup engine.
     * This pattern negates benefits of nullability check but in most cases engine must be setup.
     * If engine was not setup most likely we should just crash.
     */
    val engine get() = engineIntern ?: throw IllegalStateException("Engine was not set for system $this")

    /**
     * Property returns if true if this system already has engine
     */
    val hasEngine get() = engineIntern != null

    /**
     * Method is called by engine when this system added to it
     * Also see [Engine.addSystem]
     *
     * @param engine is engine to which system was added
     */
    open fun addedToEngine(engine: Engine) {
        require(!hasEngine) { "System already has engine!" }
        engineIntern = engine
    }

    /**
     * Method is called by engine when this system added to it
     * Also see [Engine.removeSystem]
     */
    open fun removedFromEngine() = run { engineIntern = null }

    /**
     * Method is called by engine on every update engine cycle
     * Also see [Engine.update]
     *
     * @param delta the time in seconds since the last update
     */
    abstract fun update(delta: Float)
}