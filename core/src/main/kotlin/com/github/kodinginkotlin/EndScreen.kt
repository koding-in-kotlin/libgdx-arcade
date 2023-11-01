package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxGame

import ktx.app.clearScreen
import ktx.graphics.use
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.style.label

class EndScreen(private val stage: Stage, val game: KtxGame<KtxScreen>) : KtxScreen {
    init {
        val skin = Skin(Gdx.files.internal("neut-ui/skin.json"), TextureAtlas(Gdx.files.internal("neut-ui/skin.atlas")))
//        val skin = Skin(Gdx.files.internal("neut-ui/skin.json"))
        stage.actors {
            table {
                setFillParent(true)
                defaults().pad(10f)
//                label("Hello World", skin, "title")
                label("HELLO",skin=skin)
            }
        }
    }

    private val b = SpriteBatch()

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }
}
//    override fun render(delta: Float) {
//        val skin = Skin(Gdx.files.internal("neat-ui/skin.json"), TextureAtlas(Gdx.files.internal("neat-ui/skin.atlas")))
//        t.setFillParent(true)
//
//        val label = Label("Hello There", skin, "title")
//        label.color = skin.getColor("red")
//        t.add(label)
//        //clearScreen(red = 0.0f, green = 0.0f, blue = 0.0f)
//
//        val font = BitmapFont()
//        font.data.setScale(2f, 2f)
//        font.setColor(1f, 0f, 0f, .5f)
//
//        // in the middle of the screen
//        val x0 = Gdx.graphics.width / 4f
//        val y0 = Gdx.graphics.height / 2f
//
//        b.use {
//            font.draw(
//                b,
//                "Number of diamonds is ${GameState.diamondNumber}",
//                x0,
//                y0,
//            )
//        }
//    }
//}
