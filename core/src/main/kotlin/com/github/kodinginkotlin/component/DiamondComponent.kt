package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class DiamondComponent : Component<DiamondComponent> {
    companion object : ComponentType<DiamondComponent>()

    override fun type() = DiamondComponent
}
