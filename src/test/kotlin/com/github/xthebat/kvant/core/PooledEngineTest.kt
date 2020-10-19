package com.github.xthebat.kvant.core

import com.github.xthebat.kvant.ecs.components.ForceWielder
import com.github.xthebat.kvant.ecs.components.Gatherer
import com.github.xthebat.kvant.ecs.components.LivingThing
import com.github.xthebat.kvant.ecs.systems.ForceBalanceSystem
import com.github.xthebat.kvant.ecs.systems.SelfDestructionSystem
import com.github.xthebat.kvant.entity
import com.github.xthebat.kvant.system
import com.github.xthebat.kvant.with
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.expect

internal class PooledEngineTest {
    @Test
    fun engineRunTest() {
        lateinit var jedi: Entity
        lateinit var crab: Entity

        val engine = Engine.pooled {
            jedi = entity {
                with<LivingThing> { life = 10 }
                with<ForceWielder> { force = 5 }
            }

            crab = entity {
                with<LivingThing> { life = 11 }
                with<Gatherer> { gathered = 0 }
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

    @Test
    fun engineFreeTest() {
        lateinit var jedi: Entity
        lateinit var crab: Entity
        lateinit var wolf: Entity
        lateinit var cat: Entity

        val engine = Engine.pooled {
            jedi = entity {
                with<LivingThing> { life = 10 }
                with<ForceWielder> { force = 5 }
            }

            crab = entity {
                with<LivingThing> { life = 11 }
                with<Gatherer> { gathered = 0 }
            }

            wolf = entity { with<LivingThing> { life = 20 } }
            cat = entity { with<LivingThing> { life = 5 } }

            system { ForceBalanceSystem() }
            system { SelfDestructionSystem() }
        }

        engine.removeEntity(crab)

        repeat(4) { engine.update(1f) }

        expect(1) { cat[LivingThing::class]?.life }

        val pooledCrab = engine.entity { with<LivingThing> { life = 20 } }

        assertTrue { crab === pooledCrab }
    }
}