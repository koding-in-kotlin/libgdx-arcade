package com.github.kodinginkotlin.component

import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class BodyComponent(val body: Body) : Component<BodyComponent> {
    companion object : ComponentType<BodyComponent>()

    override fun type() = BodyComponent
}
