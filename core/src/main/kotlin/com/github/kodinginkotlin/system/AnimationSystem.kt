package com.github.kodinginkotlin.system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.REVERSED
import com.github.kodinginkotlin.component.AnimationComponent
import com.github.kodinginkotlin.component.VisualComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class AnimationSystem : IteratingSystem(family { all(VisualComponent, AnimationComponent) }) {
    override fun onTickEntity(entity: Entity) {
        val animationComponent = entity[AnimationComponent]
        val visualComponent = entity[VisualComponent]

        animationComponent.timer += deltaTime
        if (
            (animationComponent.animation.playMode == NORMAL ||
                animationComponent.animation.playMode == REVERSED) &&
            animationComponent.animation.isAnimationFinished(animationComponent.timer)
        ) {
            animationComponent.onAnimationFinish()
        }
        visualComponent.region = animationComponent.animation.getKeyFrame(animationComponent.timer)
    }
}
