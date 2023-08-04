package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.World
import com.github.kodinginkotlin.component.*
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.collections.toGdxArray
import net.dermetfan.gdx.physics.box2d.ContactAdapter
import kotlin.random.Random

class DiamondSpammingSystem(
    val physicalWorld: World = inject()
) : IntervalSystem(Fixed(3f)) {
    private val diamondAnimation = diamondAnimation()


    override fun onTick() {
        val diamondX = Random.nextDouble(100.0, 600.0).toFloat()
        val diamondY = 65f
        world.entity {
            it += LocationComponent(
                diamondX,
                diamondY
            )
            val animationComponent = AnimationComponent(diamondAnimation)
            it += animationComponent
            it += VisualComponent(animationComponent.animation.getKeyFrame(Random.nextInt(9).toFloat()))
            val body = physicalWorld.body {
                position.set(diamondX, diamondY)
                box(12f, 14f, Vector2(12f, 7f))
            }
            body.userData = it
            it += BodyComponent(body)
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
