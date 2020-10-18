package com.github.xthebat.kvant.common

/**
 * Helper class for interval based systems
 *
 * @property interval defines the interval between [action] calls
 * @property action defines what to do when timer fires
 */
class IntervalTimer(val interval: Float, val action: (Float) -> Unit) {
    private var accumulator = 0f

    /**
     * Checks if interval passed and then call [action]
     *
     * @param delta the time in seconds since the last update
     */
    fun update(delta: Float) {
        accumulator += delta

        while (accumulator >= interval) {
            accumulator -= interval
            action(delta)
        }
    }
}