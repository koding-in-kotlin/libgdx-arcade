package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Keys.*
import com.github.kodinginkotlin.component.LocationComponent
import com.github.kodinginkotlin.component.PlayerStateComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class PlayerMovementSystem : IteratingSystem(family = family { all(PlayerStateComponent, LocationComponent) }) {
    override fun onTickEntity(entity: Entity) {
        if (input.isKeyPressed(D)) entity[LocationComponent].x += 100 * deltaTime
        if (input.isKeyPressed(S)) entity[LocationComponent].y -= 100 * deltaTime
        if (input.isKeyPressed(A)) entity[LocationComponent].x -= 100 * deltaTime
        if (input.isKeyPressed(W)) entity[LocationComponent].y += 100 * deltaTime
    }
}
