package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.github.kodinginkotlin.component.*
import com.github.kodinginkotlin.getTransformedCenterForRectangle
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import ktx.tiled.layer
import ktx.tiled.x
import ktx.tiled.y
import kotlin.random.Random

class DiamondSpawningSystem(
    val physicalWorld: World = inject(),
    val map: TiledMap = inject(),
) : IntervalSystem(Fixed(1f)) {
    private var flag: Boolean = false
    private val diamondAnimation = diamondAnimation()

    private var spawningPositions = run {
        val diamondsLayer = map.layer("Diamonds")
        val positions = GdxArray<Pair<Float, Float>>()
        diamondsLayer.objects.forEach { positions.add(it.x to it.y) }
        positions
    }

    init {
        for (spawningPosition in spawningPositions) {
            val (diamondX, diamondY) = spawningPosition
            world.entity {
                it += LocationComponent(
                    diamondX,
                    diamondY
                )
                it += DiamondComponent(x0 = diamondX, y0 = diamondY)
                val animationComponent = AnimationComponent(diamondAnimation)
                it += animationComponent
                it += VisualComponent(animationComponent.animation.getKeyFrame(Random.nextInt(9).toFloat()))
                val body = physicalWorld.body {
                    position.set(diamondX, diamondY)
                    box(.5f, 0.5f, Vector2(.3f, .3f)) {
                        isSensor = true
                    }
                }
                body.setTransform(Rectangle(diamondX, diamondY, 18f, 14f).getTransformedCenterForRectangle(), 0f)
                body.userData = it
                it += BodyComponent(body)

            }
        }
    }
    override fun onTick() {
////        if (flag) {
////            return
////        }
////        val diamondX = Random.nextDouble(66.0, 712.0).toFloat()
////        val diamondY = 65f
//        val (diamondX, diamondY) = spawningPositions.random()
////        diamondX += Random.nextDouble(-10.0, 10.0).toFloat()
//        world.entity {
//            it += LocationComponent(
//                diamondX,
//                diamondY
//            )
//            it += DiamondComponent(x0 = diamondX, y0 = diamondY)
//            val animationComponent = AnimationComponent(diamondAnimation)
//            it += animationComponent
//            it += VisualComponent(animationComponent.animation.getKeyFrame(Random.nextInt(9).toFloat()))
//            val body = physicalWorld.body {
//                position.set(diamondX, diamondY)
//                box(.5f, 0.5f, Vector2(.3f, .3f)) {
//                    isSensor = true
//                }
//            }
//            body.setTransform(Rectangle(diamondX, diamondY, 18f, 14f).getTransformedCenterForRectangle(), 0f)
//            body.userData = it
//            it += BodyComponent(body)
//            flag = true
//        }
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
