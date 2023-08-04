package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.github.kodinginkotlin.component.*
import com.github.kodinginkotlin.system.*
import com.github.quillraven.fleks.configureWorld
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld


class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    val map = TmxMapLoader().load("maps/arcade/tiled/Level_0.tmx")
    val camera = ShakyCamera(Gdx.graphics.width, Gdx.graphics.height).apply {
        position.x = 400f
        position.y = 250f
        zoom = .8f
        update()
    }
    val batch = SpriteBatch()
    val physicalWorld = createWorld(Vector2(0f, -7f))
//    val physicalWorld = createWorld(Vector2(0f, -9.8f * 3))

    private val world = configureWorld {
        injectables {
            add(camera)
            add(batch)
            add(map)
            add(physicalWorld)
        }
        systems {
            add(PlayerInputHandlingSystem())
            add(AnimationSystem())
            add(DiamondSpammingSystem())
            add(PhysicsSystem())
            add(PlayerCurseSystem())
            add(RenderingSystem())
            add(DebugHUDSystem())
        }
    }

    init {
        world.entity {
            val playerStateComponent = PlayerStateComponent()
            it += LocationComponent(200f, 80f)
            it += playerStateComponent
            it += AnimationComponent(playerStateComponent.animation)
            it += VisualComponent(playerStateComponent.animation.keyFrames[0])
            val body = physicalWorld.body(type = BodyDef.BodyType.DynamicBody) {
                position.set(470f, 80f)
                fixedRotation = true
                val x = box(20f, 29f, Vector2(20f, 15f)) {
                    // WHAT THE FUCK IS THIS??
                    density = .05f
//                    restitution = 0.5f
                    friction = 10f
                }
            }
            it += BodyComponent(body)
            it += ScoreComponent(Vector2(20.0f, 300.0f))

        }
    }

    override fun render(delta: Float) = world.update(delta)

    override fun dispose() {
        batch.disposeSafely()
        world.dispose()
        physicalWorld.disposeSafely()
    }
}
