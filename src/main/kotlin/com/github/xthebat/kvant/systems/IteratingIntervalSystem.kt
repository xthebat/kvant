package com.github.xthebat.kvant.systems

import com.github.xthebat.kvant.ComponentClass
import com.github.xthebat.kvant.common.IntervalTimer

/**
 * Base class for iterating/interval systems.
 * This is a mix of [IntervalSystem] and [IteratingSystem]
 *
 * @param types array of classes to get from an Engine before call process for them
 * @param interval defines the interval between [process] method calls
 * @param priority see [AbstractSystem.priority]
 *
 * @see [IntervalSystem], [IteratingSystem]
 * @since v0.1
 */
abstract class IteratingIntervalSystem(
    vararg types: ComponentClass,
    interval: Float,
    priority: Int = 0
) : IteratingSystem(*types, priority = priority) {
    private val timer = IntervalTimer(interval) { super.update(it) }

    override fun update(delta: Float) = timer.update(delta)
}