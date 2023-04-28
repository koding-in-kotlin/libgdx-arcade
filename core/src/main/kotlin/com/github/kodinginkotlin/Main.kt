package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx.*
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.graphics.use

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(Linear, Linear) }
    private val batch = SpriteBatch()
    private val renderer = ShapeRenderer()
    private val camera = ShakyCamera(graphics.width, graphics.height).apply{
        position.x = graphics.width.toFloat() / 2
        position.y = graphics.height.toFloat() / 2
        update()
    }
    private var x = 300f
    private var y = 300f
    private var flipflop = 1.0f

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 1.0f, blue = 1.0f)
        batch.use(camera) {
            it.draw(image, 100f, 160f)
        }

        // read kb, adjust y
        if (input.isKeyPressed(Input.Keys.UP)) y += 100 * delta
        if (input.isKeyPressed(Input.Keys.DOWN)) y -= 100 * delta

        // read mouse, adjust xy
        val radius = 100f;
        val maxY = graphics.height - radius
        val maxX = graphics.width - radius
        if (input.isTouched) {
            y = (graphics.height - input.y.toFloat()).coerceIn(radius, maxY)
            x = input.x.toFloat().coerceIn(radius, maxX)
        }

        // Projection matrix will be copied from the camera:
        renderer.use(ShapeRenderer.ShapeType.Filled) {
            // Operate on shapeRenderer instance
            it.color = Color.PINK
            x += delta * 500 * flipflop
            if ((x > graphics.width - radius) or (x < radius)) {
                flipflop *= -1
                camera.shake()
            }
            camera.update()
            camera.position.lerp(Vector3((graphics.width).toFloat()/2, (graphics.height).toFloat()/2, 0f), .2f)

            it.circle(x, y, radius)
        }

    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}
