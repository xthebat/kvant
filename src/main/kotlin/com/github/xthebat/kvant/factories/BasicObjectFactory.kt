package com.github.xthebat.kvant.factories

import com.github.xthebat.kvant.core.Entity
import com.github.xthebat.kvant.interfaces.Component
import com.github.xthebat.kvant.newInstance
import kotlin.reflect.KClass

class BasicObjectFactory : ObjectFactory {
    override fun entity() = Entity()

    override fun <T : Component> component(type: KClass<out T>) = type.newInstance()
}