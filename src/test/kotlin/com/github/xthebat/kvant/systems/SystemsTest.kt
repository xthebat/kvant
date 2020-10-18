package com.github.xthebat.kvant.systems

import com.github.xthebat.kvant.component
import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.ecs.components.ForceWielder
import com.github.xthebat.kvant.ecs.components.LivingThing
import com.github.xthebat.kvant.ecs.systems.ForceBalanceSystem
import com.github.xthebat.kvant.ecs.systems.SelfDestructionSystem
import org.junit.Test
import kotlin.test.expect


internal class SystemsTest {
    @Test
    fun iteratingSystemProcessTest() {
        val sith = Entity.create { component { ForceWielder(-10) } }
        val jedi = Entity.create { component { ForceWielder(5) } }

        val entities = listOf(sith, jedi)
        val system = ForceBalanceSystem()

        repeat(5) { entities.forEach { system.process(it, 0f) } }

        expect(0) { jedi[ForceWielder::class]!!.force }
        expect(-5) { sith[ForceWielder::class]!!.force }

        repeat(5) { entities.forEach { system.process(it, 0f) } }

        expect(0) { jedi[ForceWielder::class]!!.force }
        expect(0) { sith[ForceWielder::class]!!.force }
    }

    @Test
    fun intervalSystemProcessTest() {
        val crab = Entity.create {
            component { ForceWielder(5) }
            component { LivingThing(10) }
        }

        expect(5) { crab[ForceWielder::class]?.force }
        expect(10) { crab[LivingThing::class]?.life }

        val system = object : IntervalSystem(1f) {
            override fun updateInterval() {
                val living = crab[LivingThing::class] ?: return
                if (living.life > 0) living.life--
            }
        }

        repeat(9) { system.update(1f) }

        expect(1) { crab[LivingThing::class]?.life }
    }
}