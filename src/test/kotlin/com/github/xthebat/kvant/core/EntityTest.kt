package com.github.xthebat.kvant.core

import com.github.xthebat.kvant.component
import com.github.xthebat.kvant.ecs.components.ForceWielder
import com.github.xthebat.kvant.ecs.components.Gatherer
import com.github.xthebat.kvant.interfaces.Component
import org.junit.Test
import kotlin.test.expect

internal class EntityTest {

    @Test
    fun componentAddGetTest() {
        val expected = Gatherer()

        val entity = Entity.create {
            component { expected }
            component { object : Component { } }
        }

        expect(expected) { entity[Gatherer::class] }
    }

    @Test
    fun componentRemoveTest() {
        val entity = Entity.create {
            component { Gatherer() }
            component { object : Component { } }
        }

        entity.remove(Gatherer::class)

        expect(null) { entity[Gatherer::class] }
    }

    @Test
    fun componentRemoveAllTest() {
        val entity = Entity.create {
            component { Gatherer() }
            component { object : Component { } }
        }

        entity.removeAll()

        expect(0) { entity.size }
    }

    @Test
    fun componentContainsTest() {
        val entity = Entity.create {
            component { Gatherer() }
            component { object : Component { } }
        }

        expect(true) { Gatherer::class in entity }
    }

    @Test
    fun componentNotifyTest() {
        var addNotificationObject: Any? = null
        var removeNotificationObject: Any? = null

        val wielder = ForceWielder()

        val entity = Entity.create {
            component { Gatherer() }
            component { object : Component { } }

            notifyAdded { addNotificationObject = it }
            notifyRemoved { removeNotificationObject = it }
        }

        expect(wielder) { entity.add(wielder); addNotificationObject }
        expect(wielder) { entity.remove(ForceWielder::class); removeNotificationObject }
    }

    @Test
    fun componentReplaceTest() {
        val expected = ForceWielder()
        val entity = Entity.create {
            component { ForceWielder() }
            component { expected }
        }
        expect(expected) { entity[ForceWielder::class] }
    }
}