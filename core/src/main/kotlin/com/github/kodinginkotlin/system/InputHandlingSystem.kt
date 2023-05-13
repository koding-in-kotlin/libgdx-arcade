package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Keys.*
import com.github.kodinginkotlin.component.PlayerState
import com.github.kodinginkotlin.component.PlayerStateEnum.*
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class InputHandlingSystem : IteratingSystem(family { all(PlayerState) }) {
    override fun onTickEntity(entity: Entity) {
        val st = entity[PlayerState]

        if (input.isKeyJustPressed(D)) st.movingRight = true
        else if (input.isKeyJustPressed(A)) st.movingRight = false

        if (input.isKeyPressed(CONTROL_LEFT) || input.isKeyPressed(CONTROL_RIGHT)) st.state = ATTACKING
        else if (input.isKeyPressed(D) || input.isKeyPressed(A)) st.state = RUNNING
        else st.state = IDLE
    }
}
