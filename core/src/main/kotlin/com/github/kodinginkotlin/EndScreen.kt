package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.VisUI
import ktx.actors.onChange
import ktx.app.KtxGame

import ktx.app.clearScreen
import ktx.graphics.use
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable
import ktx.style.color
import ktx.style.label
import kotlin.system.exitProcess

class EndScreen(private val stage: Stage, val game: KtxGame<KtxScreen>) : KtxScreen {
    init {
    }

    private fun processExit() {
        exitProcess(0)
    }

    private fun processGoBack() {
        println("OHUOTHONUHUOSNHNUOHN")
        game.setScreen<FirstScreen>()
    }

    private val b = SpriteBatch()

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.actors {

            visTable {
                setFillParent(true)
                defaults().pad(10f)
                visLabel("Number ${GameState} of diamonds is ${GameState.scoreText}") {
                    style.fontColor = Color.RED
                }
                row()
                visLabel("You robbed 1 caravan. Congrats!")
                row()
                textButton("Play again") {
                    onChange { processGoBack() }
                }
                row()
                textButton("Exit") {
                    onChange { processExit() }
                }
            }
        }

        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}
