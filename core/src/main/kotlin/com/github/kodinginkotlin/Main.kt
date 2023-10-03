package com.github.kodinginkotlin

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.viewport.FillViewport
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
import ktx.box2d.earthGravity
import ktx.tiled.*


class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

const val PPM = 32f

class FirstScreen : KtxScreen {
    val map = TmxMapLoader().load("maps/arcade/tiled/Level_0.tmx", TmxMapLoader.Parameters())
    val camera = ShakyCamera(map.width, map.height).apply {
//        position.x = 400f / PPM
//        position.y = 250f / PPM
        neutralPos = Vector2(position.x, position.y)
        update()
    }
    val viewport = FillViewport(map.width.toFloat(), map.height.toFloat(), camera).apply {
        this.apply(true)
    }
    val batch = SpriteBatch()

    //        val physicalWorld = createWorld(Vector2(0f, -.1f))
    val physicalWorld = createWorld(earthGravity)

    private val world = configureWorld {
        injectables {
            add(camera)
            add(batch)
            add(map)
            add(physicalWorld)
            add(viewport)
        }
        systems {
            add(PlayerInputHandlingSystem())
            add(AnimationSystem())
            add(DiamondSpawningSystem())
            add(PhysicsSystem())
            add(PlayerCurseSystem())
            add(RenderingSystem())
            add(DebugHUDSystem())
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        world.entity {
            val playerStateComponent = PlayerStateComponent()
            it += playerStateComponent
            it += AnimationComponent(playerStateComponent.animation)
            it += VisualComponent(playerStateComponent.animation.keyFrames[0])
            val body = map.layer("Hero").objects.first().let {
                physicalWorld.body(BodyDef.BodyType.DynamicBody) {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(.6f, .7f, Vector2(.6f, .4f)) {
                        density = 2.7f
                        friction = .8f
                        restitution = 0.01f
                    }
                    fixedRotation = true
                }.apply {
                    setTransform(Rectangle(it.x, it.y, it.width, it.height).getTransformedCenterForRectangle(), 0f)
                }
            }
            it += LocationComponent(body.position.x, body.position.y)
            it += BodyComponent(body)
            it += ScoreComponent(Vector2(20.0f, 300.0f))
            body.userData = it
        }
    }

    override fun render(delta: Float) = world.update(delta)

    override fun dispose() {
        batch.disposeSafely()
        world.dispose()
        physicalWorld.disposeSafely()
    }
}

fun Rectangle.getTransformedCenterForRectangle(): Vector2 =
    Vector2().let(this::getCenter).scl(1 / PPM)
