package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.github.kodinginkotlin.GameState
import com.github.kodinginkotlin.PPM
import com.github.kodinginkotlin.ShakyCamera
import com.github.kodinginkotlin.component.*
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

class RenderingSystem(
    private val batch: SpriteBatch = inject(),
    private val camera: ShakyCamera = inject(),
    map: TiledMap = inject(),
    private val physicalWorld: World = inject(),
) : IntervalSystem() {
    private var currentTime = 0f
    private val visuals = world.family { all(LocationComponent, VisualComponent).none(DeadComponent) }
    private val renderer = OrthogonalTiledMapRenderer(map, 1 / PPM, batch)
    private val debugRenderer = Box2DDebugRenderer()
    private val huds = world.family { all(ScoreComponent) }

    //private val font = BitmapFont(Gdx.files.internal("ui/font.fnt"), Gdx.files.internal("ui/uiskin.png"),false)
    private val font = BitmapFont().apply{data.scale(0.00001f)}


    override fun onTick() {
        clearScreen(red = 0.0f, green = 0.0f, blue = 0.0f)
        currentTime += deltaTime
        renderer.setView(camera)
        renderer.render()
        batch.use(camera) { b ->
            visuals.forEach {
                val location = it[LocationComponent]
                val region = it[VisualComponent].region
                b.draw(
                    region,
                    location.x,
                    location.y,
                    0f,
                    0f,
                    region.regionWidth.toFloat(),
                    region.regionHeight.toFloat(),
                    1 / PPM,
                    1 / PPM,
                    0f
                )
            }
            huds.forEach {
                val hud = it[ScoreComponent]
                font.draw(b, GameState.scoreText, hud.location.x / PPM, hud.location.y / PPM)
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
            debugRenderer.render(physicalWorld, camera.combined)
        }

        camera.update()

    }

    override fun onDispose() {
        renderer.disposeSafely()
    }
}
