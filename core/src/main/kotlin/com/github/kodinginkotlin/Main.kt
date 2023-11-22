package com.github.kodinginkotlin

import MainMenu
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.FillViewport
import com.github.kodinginkotlin.component.*
import com.github.kodinginkotlin.system.*
import com.github.quillraven.fleks.configureWorld
import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.scene2d.Scene2DSkin
import ktx.tiled.*


class Main : KtxGame<KtxScreen>() {
    override fun create() {
//        VisUI.load(Gdx.files.internal("assets/neut-ui/skin.json"))
        //VisUI.load(Gdx.files.internal("neut-ui/skin.json"))
        VisUI.load(Gdx.files.internal("ui/uiskin.json"))
        Scene2DSkin.defaultSkin = VisUI.getSkin()

        KtxAsync.initiate()
        addScreen(MainMenu(Stage(), this))
        addScreen(FirstScreen(this))
//        GameState.diamondNumber = 35124
        addScreen(EndScreen(Stage(), this))
        setScreen<MainMenu>()
//        setScreen<EndScreen>()
    }
}

const val PPM = 32f

class FirstScreen(main: KtxGame<KtxScreen>) : KtxScreen {
    val map = TmxMapLoader().load("maps/arcade/tiled/0002_Level_1.tmx", TmxMapLoader.Parameters())
    val camera = ShakyCamera(map.width, map.height).apply {
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
            add(main)
        }
        systems {
            add(PlayerInputHandlingSystem())
            add(AnimationSystem())
            add(DiamondSpawningSystem())
            add(DiamondBobbingSystem())
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
                        density = 1.0f
                        friction = 1f
//                        restitution = 0.1f
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
