package com.github.kodinginkotlin.system

import com.github.kodinginkotlin.component.*
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World

class DebugHUDSystem : IteratingSystem(World.family {
    all(
        PlayerStateComponent,BodyComponent,AnimationComponent
    )
}) {
    override fun onTickEntity(entity: Entity) {
        val e = entity[PlayerStateComponent]
        val state = e.state
        val direction = e.directionState
        val velo = entity[BodyComponent].body.massData
        val ac = entity[AnimationComponent]
        println("$state, $direction, $velo, ${ac.timer}, hello there")

    }
}
