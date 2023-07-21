package com.github.kodinginkotlin.system

import com.github.kodinginkotlin.component.LocationComponent
import com.github.kodinginkotlin.component.PlayerStateComponent
import com.github.kodinginkotlin.component.ScoreComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class PlayerCurseSystem : IntervalSystem(Fixed(5f)) {
    val score = world.family{all(ScoreComponent)}

    override fun onTick() {
        score.forEach {
            val s = it[ScoreComponent]
            s.score -= 5
        }
    }
}
