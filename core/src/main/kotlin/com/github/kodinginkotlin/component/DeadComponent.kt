package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class DeadComponent() : Component<DeadComponent> {
    companion object : ComponentType<DeadComponent>()

    override fun type() = DeadComponent
}
