package com.github.xthebat.kvant.ecs.systems

import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.ecs.components.LivingThing
import com.github.xthebat.kvant.systems.IteratingIntervalSystem

class SelfDestructionSystem : IteratingIntervalSystem(LivingThing::class, interval = 1f) {
    override fun process(entity: Entity, delta: Float) {
        val living = entity[LivingThing::class] ?: return
        if (living.life > 0) living.life--
    }
}