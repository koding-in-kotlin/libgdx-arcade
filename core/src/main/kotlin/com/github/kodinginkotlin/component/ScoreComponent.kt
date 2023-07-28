package com.github.kodinginkotlin.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class ScoreComponent(var location: Vector2) : Component<ScoreComponent> {
    companion object : ComponentType<ScoreComponent>()


    override fun type(): ComponentType<ScoreComponent> = ScoreComponent
}
