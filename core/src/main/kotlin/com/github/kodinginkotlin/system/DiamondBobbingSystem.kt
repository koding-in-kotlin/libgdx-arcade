package com.github.kodinginkotlin.system

import com.badlogic.gdx.math.Vector2
import com.github.kodinginkotlin.component.BodyComponent
import com.github.kodinginkotlin.component.DiamondComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class DiamondBobbingSystem : IteratingSystem(family { all(LocationComponent, DiamondComponent, BodyComponent) }) {

    private var t: Float = 0f

    override fun onTickEntity(entity: Entity) {
        val d = entity[DiamondComponent]
        val bc = entity[BodyComponent]

        t += deltaTime

        val blerb = d.bobSpread * Math.sin((d.bobFreq * t).toDouble()).toFloat()
        bc.body.setTransform(Vector2(bc.body.position.x, bc.body.position.y + blerb), 0f)
    }
}
