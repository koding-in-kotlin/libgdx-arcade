package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Location(var x: Float, var y: Float) : Component<Location> {
    companion object : ComponentType<Location>()

    override fun type() = Location
}
