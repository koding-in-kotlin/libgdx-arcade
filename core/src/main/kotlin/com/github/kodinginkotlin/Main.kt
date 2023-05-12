package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx.*
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.collections.toGdxArray
import ktx.graphics.use

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
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

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 1.0f, blue = 1.0f)
        renderer.setView(camera)
        stateTime += delta; // Accumulate elapsed animation time
        val character =
            (
                if (input.isKeyPressed(CONTROL_LEFT) && input.isKeyPressed(A)) attackLeftAnimation
                else if (input.isKeyPressed(CONTROL_LEFT)) attackRightAnimation
                else if (input.isKeyPressed(D)) runRightAnimation
                else if (input.isKeyPressed(A)) runLeftAnimation
                else idleAnimation)
                .getKeyFrame(stateTime, true)
        batch.use(camera) {
            renderer.render()
            it.draw(character, x, y)
        }

        // read kb, adjust y
        if (input.isKeyPressed(D)) x += 100 * delta
        if (input.isKeyPressed(W)) y += 100 * delta
        if (input.isKeyPressed(S)) y -= 100 * delta
        if (input.isKeyPressed(A)) x -= 100 * delta

        // read mouse, adjust xy
        val radius = 100f;
        val maxY = graphics.height - radius
        val maxX = graphics.width - radius
        if (input.isTouched) {
            y = (graphics.height - input.y.toFloat()).coerceIn(radius, maxY)
            x = input.x.toFloat().coerceIn(radius, maxX)
        }


    }

    override fun dispose() {
        batch.disposeSafely()
        map.disposeSafely()
        renderer.disposeSafely()

    }
}
