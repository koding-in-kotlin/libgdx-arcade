package com.github.kodinginkotlin

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.collections.toGdxArray

class Location(var x: Float = 0f, var y: Float = 0f) : Component

class Animation(var animation: Animation<TextureRegion> = Animation(0f)) : Component

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    val engine = PooledEngine()
    private val batch = SpriteBatch()

    private val camera = ShakyCamera(graphics.width, graphics.height).apply {
        position.x = 256f
        position.y = 160f
        zoom = .5f
        update()
    }
    private var x = 100f
    private var y = 100f
    val map = TmxMapLoader().load("maps/arcade/tiled/Level_0.tmx")
    val renderer = OrthogonalTiledMapRenderer(map)
    val idleAnimation = idleAnimation()
    val runRightAnimation = runAnimation()
    val runLeftAnimation = runAnimation(true)
    val attackRightAnimation = attackAnimation()
    val attackLeftAnimation = attackAnimation(true)

    private fun idleAnimation(): Animation<TextureRegion> {
        val texture = Texture(files.internal("kings_and_pigs/01-King Human/Idle (78x58).png"))
        val idleFrames =
            TextureRegion
                .split(texture, texture.width / 11, texture.height)[0]
                .toGdxArray()
        return Animation(.05f, idleFrames)
    }

    private fun attackAnimation(flip: Boolean = false): Animation<TextureRegion> {
        val texture = Texture(files.internal("kings_and_pigs/01-King Human/Attack (78x58).png"))
        val idleFrames =
            TextureRegion
                .split(texture, texture.width / 3, texture.height)[0]
                .map { it.also { it.flip(flip, false) } }
                .toGdxArray()
        return Animation(.05f, idleFrames)
    }

    private fun runAnimation(flip: Boolean = false): Animation<TextureRegion> {
        val texture = Texture(files.internal("kings_and_pigs/01-King Human/Run (78x58).png"))
        val idleFrames =
            TextureRegion
                .split(texture, texture.width / 8, texture.height)[0]
                .map { it.also { it.flip(flip, false) } }
                .toGdxArray()
        return Animation(.05f, idleFrames)
    }

    var stateTime = 0f

    class PlayerState(var state: StateEnum = StateEnum.IDLE) : Component

    init {
        val player = engine.entity {
            with<Location> {
                x = 100f
                y = 100f
            }
            with<com.github.kodinginkotlin.Animation> {
                animation = idleAnimation
            }
            with<PlayerState>()
        }
        engine.addSystem(RenderingSystem())
        engine.addSystem(InputHandlingSystem())

    }

    override fun render(delta: Float) {

        engine.update(delta)
    }

    override fun dispose() {
        batch.disposeSafely()
        map.disposeSafely()
        renderer.disposeSafely()

    }
}

enum class StateEnum {
    IDLE, RUNNING
}
