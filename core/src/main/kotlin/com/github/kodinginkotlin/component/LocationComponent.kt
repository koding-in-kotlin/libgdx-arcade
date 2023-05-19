package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class LocationComponent(var x: Float, var y: Float) : Component<LocationComponent> {
    companion object : ComponentType<LocationComponent>()

    override fun type() = LocationComponent
}
