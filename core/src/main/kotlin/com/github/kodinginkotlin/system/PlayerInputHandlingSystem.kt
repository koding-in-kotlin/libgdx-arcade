package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Keys.*
import com.github.kodinginkotlin.component.AnimationComponent
import com.github.kodinginkotlin.component.BodyComponent
import com.github.kodinginkotlin.component.PlayerDirectionEnum
import com.github.kodinginkotlin.component.PlayerStateComponent
import com.github.kodinginkotlin.component.PlayerStateEnum.*
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class PlayerInputHandlingSystem : IteratingSystem(family { all(PlayerStateComponent, AnimationComponent) }) {
    var timeSinceLastAttack = 0f
    var runningTimer = 0f
    var runningDelta = 0.15f
    override fun onTickEntity(entity: Entity) {
        timeSinceLastAttack += deltaTime
        val st = entity[PlayerStateComponent]
        val previousState = st.state
        val previousDirection = st.directionState
        val previousAnimation = st.animation
        if (previousState == RUNNING) {
            runningTimer += deltaTime
        }
        if (runningTimer > runningDelta) {
            st.state = IDLE
            runningTimer = 0f
        }
        // JUST pressed
        if (input.isKeyJustPressed(RIGHT)) {
            st.directionState = PlayerDirectionEnum.RIGHT
        } else if (input.isKeyJustPressed(LEFT)) st.directionState = PlayerDirectionEnum.LEFT

        // Kept pressed
        if ((input.isKeyPressed(CONTROL_LEFT) ||
                input.isKeyPressed(CONTROL_RIGHT)
                ) &&
            previousState != ATTACKING &&
            timeSinceLastAttack > 0.3f
        ) {
            st.state = ATTACKING
            timeSinceLastAttack = 0f
        } else if (input.isKeyPressed(RIGHT) || input.isKeyPressed(LEFT)) st.state = RUNNING
//        else st.state = IDLE

        val stateChanged = st.state != previousState
        val directionChanged = previousDirection != st.directionState

        if (stateChanged || directionChanged) { //|| previousDirection != PlayerDirectionEnum.RIGHT) { // || previousDirection != PlayerDirectionEnum.LEFT) {
            entity[AnimationComponent].animation = entity[PlayerStateComponent].animation
            entity[AnimationComponent].timer = 0f

            if (st.state == ATTACKING) {
                entity[AnimationComponent].onAnimationFinish = {
                    entity[AnimationComponent].animation = previousAnimation
                    entity[AnimationComponent].timer = 0f
                    st.state = previousState
                }
            }
        }
    }

    override fun onDispose() {
        family.forEach { it[PlayerStateComponent].dispose() }
    }
}
