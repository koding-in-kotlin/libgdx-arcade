package com.github.kodinginkotlin.system

import com.badlogic.gdx.math.Vector2
import com.github.kodinginkotlin.component.BodyComponent
import com.github.kodinginkotlin.component.DiamondComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import kotlin.math.sin

class DiamondBobbingSystem : IntervalSystem() {

    private var t: Float = 0f
    val family = family { all(LocationComponent, DiamondComponent, BodyComponent) }

    override fun onTick() {
        t += deltaTime
        var counter = 0
        family.forEach {
            val d = it[DiamondComponent]
            val bc = it[BodyComponent]
            val location = it[LocationComponent]


            val blerb = d.bobSpread * sin((d.bobFreq * t).toDouble() + counter.toFloat() * 5).toFloat()
            bc.body.setTransform(Vector2(bc.body.position.x, bc.body.position.y + blerb), 0f)
            counter++

        }
    }
}
