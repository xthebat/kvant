package com.github.xthebat.kvant.factories

import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.interfaces.Component
import kotlin.reflect.KClass

interface ObjectFactory {
    fun entity(): Entity

    fun <T: Component> component(type: KClass<out T>): T

    fun clearCache() = Unit

    fun freeEntity(entity: Entity) = Unit

    fun freeComponent(component: Component) = Unit
}