package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.github.kodinginkotlin.component.Location
import com.github.kodinginkotlin.component.PlayerState
import com.github.kodinginkotlin.system.PlayerInputHandlingSystem
import com.github.kodinginkotlin.system.RenderingSystem
import com.github.quillraven.fleks.world
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync


class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    val camera = ShakyCamera(Gdx.graphics.width, Gdx.graphics.height).apply {
        position.x = 256f
        position.y = 160f
        zoom = .5f
        update()
    }
    val batch = SpriteBatch()

    val world = world {
        injectables {
            add(camera)
            add(batch)
        }
        systems {
            add(PlayerInputHandlingSystem())
            add(RenderingSystem())
        }
    }


    val map = TmxMapLoader().load("maps/arcade/tiled/Level_0.tmx")
    val renderer = OrthogonalTiledMapRenderer(map)

    init {
        world.entity {
            it += Location(50f, 50f)
            it += PlayerState()
        }
    }

    override fun render(delta: Float) = world.update(delta)

    override fun dispose() {
        batch.disposeSafely()
        world.dispose()
    }
}


