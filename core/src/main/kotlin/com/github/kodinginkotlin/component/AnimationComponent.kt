package com.github.kodinginkotlin.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class AnimationComponent(
    var animation: Animation<TextureRegion>,
    var timer: Float = 0f,
    var onAnimationFinish: () -> Unit = {}
) : Component<AnimationComponent> {
    companion object : ComponentType<AnimationComponent>()

    override fun type() = AnimationComponent
}
