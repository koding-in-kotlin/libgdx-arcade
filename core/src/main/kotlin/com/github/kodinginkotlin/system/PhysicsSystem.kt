package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.github.kodinginkotlin.component.BodyComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.fixture
import ktx.tiled.*

class PhysicsSystem(
    private val physicalWorld: World = inject(),
    map: TiledMap = inject()
) : IntervalSystem(interval = Fixed(1 / 60f)) {

    init {
        val entitiesLayer = map.layer("Entities")
        entitiesLayer.objects.forEach {
            if (it is RectangleMapObject){
                physicalWorld.body {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(it.width, it.height)
                }
            }
        }
    }

    val family = world.family { all(BodyComponent, LocationComponent) }
    override fun onTick() {
        physicalWorld.step(1 / 60f, 6, 2)
        family.forEach {
            val b = it[BodyComponent].body
            it[LocationComponent].x = b.position.x
            it[LocationComponent].y = b.position.y
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) it[BodyComponent].body.setLinearVelocity(0f, 20f)
        }
    }
}
