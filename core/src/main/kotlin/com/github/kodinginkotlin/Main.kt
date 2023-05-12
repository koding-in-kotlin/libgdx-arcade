package com.github.kodinginkotlin

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
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
import ktx.app.clearScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.collections.toGdxArray
import ktx.graphics.use

class Location(var x: Float = 0f, var y: Float = 0f) : Component

class Animation(var animation: Animation<TextureRegion> = Animation(0f)) : Component


class RenderingSystem : EntitySystem(Int.MAX_VALUE) {
    private lateinit var entities: ImmutableArray<Entity>
    private val batch = SpriteBatch()
    private val camera = ShakyCamera(graphics.width, graphics.height).apply {
        position.x = 256f
        position.y = 160f
        zoom = .5f
        update()
    }
    val animationComponents = ComponentMapper.getFor(com.github.kodinginkotlin.Animation::class.java)
    val locationComponents = ComponentMapper.getFor(Location::class.java)

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(
            Family.all(com.github.kodinginkotlin.Animation::class.java, Location::class.java).get()
        )
    }

    override fun update(deltaTime: Float) {
        clearScreen(red = 0.7f, green = 1.0f, blue = 1.0f)
//        renderer.setView(camera)
        batch.use {
            for (entity in entities) {
                val anim = animationComponents.get(entity).animation
                val location = locationComponents.get(entity)
                it.draw(anim.getKeyFrame(0f), location.x, location.y)
            }
        }
    }
}

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
    init {
        val player = engine.entity {
            with<Location> {
                x = 100f
                y = 100f
            }
            with<com.github.kodinginkotlin.Animation> {
                animation = idleAnimation
            }
        }
        engine.addSystem(RenderingSystem())

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
