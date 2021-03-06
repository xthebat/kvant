package com.github.xthebat.kvant.core

import com.github.xthebat.kvant.component
import com.github.xthebat.kvant.ecs.components.ForceWielder
import com.github.xthebat.kvant.ecs.components.Gatherer
import com.github.xthebat.kvant.ecs.components.LivingThing
import com.github.xthebat.kvant.ecs.systems.ForceBalanceSystem
import com.github.xthebat.kvant.ecs.systems.SelfDestructionSystem
import com.github.xthebat.kvant.entity
import com.github.xthebat.kvant.system
import com.github.xthebat.kvant.with
import org.junit.Test
import kotlin.test.expect

internal class EngineTest {
    @Test
    fun engineRunTest() {
        lateinit var jedi: Entity
        lateinit var crab: Entity

        val engine = Engine.basic {
            jedi = entity {
                with<LivingThing> { life = 10 }
                with<ForceWielder> { force = 5 }
            }

            crab = entity {
                with<LivingThing> { life = 11 }
                with<Gatherer> {  }
            }

            system { ForceBalanceSystem() }
            system { SelfDestructionSystem() }
        }

        repeat(4) { engine.update(1f) }

        expect(6) { jedi[LivingThing::class]?.life }
        expect(1) { jedi[ForceWielder::class]?.force }
        expect(7) { crab[LivingThing::class]?.life }
        expect(0) { crab[Gatherer::class]?.gathered }
    }
}