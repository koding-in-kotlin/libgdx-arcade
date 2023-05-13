package com.github.kodinginkotlin.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.kodinginkotlin.ShakyCamera
import com.github.kodinginkotlin.component.Location
import com.github.kodinginkotlin.component.PlayerState
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

class RenderingSystem(
    private val batch: SpriteBatch = inject(),
    private val camera: ShakyCamera = inject()
) : IntervalSystem() {
    private var currentTime = 0f
    val mybois = world.family { all(Location, PlayerState) }

    override fun onTick() {
        clearScreen(red = 0.7f, green = 1.0f, blue = 1.0f)
        currentTime += deltaTime
        batch.use(camera) { b ->
            mybois.forEach {
                val location = it[Location]
                val ani = it[PlayerState].animation
                val keyFrame = ani.getKeyFrame(currentTime)
                b.draw(keyFrame, location.x, location.y)
            }
        }
    }
}
