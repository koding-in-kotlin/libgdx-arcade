package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.box2d.World
import com.github.kodinginkotlin.component.BodyComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.kodinginkotlin.component.PlayerStateComponent
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject

class PhysicsSystem(private val physicalWorld: World = inject()): IntervalSystem(interval = Fixed(1/60f)) {

    val family = world.family { all(BodyComponent, LocationComponent) }
    override fun onTick() {
        physicalWorld.step(1/60f, 6, 2)
        family.forEach {
            it[LocationComponent].x =it[BodyComponent].body.position.x
            it[LocationComponent].y =it[BodyComponent].body.position.y
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) it[BodyComponent].body.setLinearVelocity(0f, 20f)
        }
    }
}
