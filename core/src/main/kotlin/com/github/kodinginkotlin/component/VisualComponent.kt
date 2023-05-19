package com.github.kodinginkotlin.component

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class VisualComponent(var region: TextureRegion) : Component<VisualComponent> {
    companion object : ComponentType<VisualComponent>()

    override fun type(): ComponentType<VisualComponent> = VisualComponent
}
