package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class WallComponent : Component<WallComponent> {
    companion object : ComponentType<WallComponent>()

    override fun type() = WallComponent
}
