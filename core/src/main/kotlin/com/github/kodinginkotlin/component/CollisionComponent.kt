package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class CollisionComponent() : Component<CollisionComponent> {
    companion object : ComponentType<CollisionComponent>()

    override fun type() = CollisionComponent
}
