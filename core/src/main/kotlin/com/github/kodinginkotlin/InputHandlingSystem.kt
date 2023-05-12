package com.github.kodinginkotlin

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Keys.D
import com.github.kodinginkotlin.FirstScreen.PlayerState
import com.github.kodinginkotlin.StateEnum.IDLE
import com.github.kodinginkotlin.StateEnum.RUNNING
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor

class InputHandlingSystem : IteratingSystem(allOf(PlayerState::class).get()) {
    private val psm = mapperFor<PlayerState>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[psm]?.state = if (input.isKeyPressed(D)) RUNNING else IDLE
        println("HERE $deltaTime")
    }
}
