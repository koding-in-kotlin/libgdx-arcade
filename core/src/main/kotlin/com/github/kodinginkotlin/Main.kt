package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.graphics.color
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
    private val shh = ShapeRenderer()
    private val camera = OrthographicCamera()
    private var x = 300f
    private var y = 300f
    private var flipflop = 1.0f

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        batch.use {
            it.draw(image, 100f, 160f)

        }

        // read kb, adjust y
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) y += 100 * delta;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) y -= 100 * delta;


        // Projection matrix will be copied from the camera:
        shh.use(ShapeRenderer.ShapeType.Filled) {
            // Operate on shapeRenderer instance
            it.setColor(Color.PINK)
            x += delta * 100 * flipflop
            if ((x > 600) or (x < 100f)) {
                flipflop *= -1
            }

            it.circle(x, y, 100f)
        }

    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}
