package com.github.kodinginkotlin

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.clearScreen
import ktx.ashley.allOf
import ktx.graphics.use

class RenderingSystem : EntitySystem(Int.MAX_VALUE) {
    private lateinit var entities: ImmutableArray<Entity>
    private val batch = SpriteBatch()
    private val camera = ShakyCamera(Gdx.graphics.width, Gdx.graphics.height).apply {
        position.x = 256f
        position.y = 160f
        zoom = .5f
        update()
    }
    val animationComponents = ComponentMapper.getFor(Animation::class.java)
    val locationComponents = ComponentMapper.getFor(Location::class.java)

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(allOf(Animation::class, Location::class).get())
    }

    override fun update(deltaTime: Float) {
        clearScreen(red = 0.7f, green = 1.0f, blue = 1.0f)
        batch.use(camera) {
            for (entity in entities) {
                val anim = animationComponents.get(entity).animation
                val location = locationComponents.get(entity)
                it.draw(anim.getKeyFrame(0f), location.x, location.y)
            }
        }
    }
}
