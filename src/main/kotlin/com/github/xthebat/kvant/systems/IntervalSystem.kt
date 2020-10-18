package com.github.xthebat.kvant.systems

import com.github.xthebat.kvant.common.IntervalTimer

/**
 * Base class for interval systems.
 * It calls [updateInterval] method after specified [interval] timeout reaches.
 *
 * @property interval defines the interval between [updateInterval] method calls
 * @param priority see [AbstractSystem.priority]
 * @since v0.1
 */
abstract class IntervalSystem(val interval: Float, priority: Int = 0) : AbstractSystem(priority) {
    private val timer = IntervalTimer(interval) { updateInterval() }

    override fun update(delta: Float) = timer.update(delta)

    /**
     * Method should defines what to do when interval timer is fire.
     */
    abstract fun updateInterval()
}