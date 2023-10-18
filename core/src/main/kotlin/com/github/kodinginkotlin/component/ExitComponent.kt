package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class ExitComponent : Component<ExitComponent> {
    companion object : ComponentType<ExitComponent>()

    override fun type() = ExitComponent
}
