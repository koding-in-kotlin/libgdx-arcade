package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.kodinginkotlin.component.AnimationComponent
import com.github.kodinginkotlin.component.BodyComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.kodinginkotlin.component.VisualComponent
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import ktx.collections.toGdxArray
import kotlin.random.Random

class DiamondSpammingSystem : IntervalSystem(Fixed(3f)) {
    private val diamondAnimation = diamondAnimation()
    override fun onTick() {
        world.entity {
            it += LocationComponent(
                Random.nextDouble(100.0, 200.0).toFloat(),
                70.0f
            )
            val animationComponent = AnimationComponent(diamondAnimation)
            it += animationComponent
            it += VisualComponent(animationComponent.animation.getKeyFrame(Random.nextInt(9).toFloat()))
        }
    }

    private fun diamondAnimation(): Animation<TextureRegion> {
        val texture =
            Texture(Gdx.files.internal("kings_and_pigs/12-Live and Coins/Big Diamond Idle (18x14).png"))
        val idleFrames =
            TextureRegion.split(texture, texture.width / 10, texture.height)[0]
                .map { it.also { it.flip(false, false) } }
                .toGdxArray()
        return Animation(.05f, idleFrames, Animation.PlayMode.LOOP)

    }
}
