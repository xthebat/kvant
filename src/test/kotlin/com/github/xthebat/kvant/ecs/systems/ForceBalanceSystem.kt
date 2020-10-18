package com.github.xthebat.kvant.ecs.systems

import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.ecs.components.ForceWielder
import com.github.xthebat.kvant.systems.IteratingSystem

class ForceBalanceSystem : IteratingSystem(ForceWielder::class) {
    override fun process(entity: Entity, delta: Float) {
        val wielder = entity[ForceWielder::class] ?: return
        when {
            wielder.force > 0 -> wielder.force--
            wielder.force < 0 -> wielder.force++
        }
    }
}