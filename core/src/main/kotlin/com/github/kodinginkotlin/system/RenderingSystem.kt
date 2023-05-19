package com.github.kodinginkotlin.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.kodinginkotlin.ShakyCamera
import com.github.kodinginkotlin.component.DeadComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.kodinginkotlin.component.VisualComponent
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.app.clearScreen
import ktx.graphics.use

class RenderingSystem(
    private val batch: SpriteBatch = inject(),
    private val camera: ShakyCamera = inject()
) : IntervalSystem() {
    private var currentTime = 0f
    private val visuals = world.family { all(LocationComponent, VisualComponent).none(DeadComponent) }

    override fun onTick() {
        clearScreen(red = 0.7f, green = 1.0f, blue = 1.0f)
        currentTime += deltaTime
        batch.use(camera) { b ->
            visuals.forEach {
                val location = it[LocationComponent]
                b.draw(it[VisualComponent].region, location.x, location.y)
            }
        }
    }
}
