package com.github.kodinginkotlin.system

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.github.kodinginkotlin.component.CollisionComponent
import com.github.kodinginkotlin.component.DeadComponent
import com.github.kodinginkotlin.component.LocationComponent
import com.github.kodinginkotlin.component.VisualComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class CollisionDetectionSystem :
    IteratingSystem(family = family {
        all(
            CollisionComponent,
            LocationComponent,
            VisualComponent
        ).none(DeadComponent)
    }) {

    override fun onTickEntity(entity: Entity) {
        family.forEach { other ->

            val intersects = other.id != entity.id && Intersector.overlaps(
                Rectangle(
                    entity[LocationComponent].x,
                    entity[LocationComponent].y,
                    entity[VisualComponent].region.regionWidth.toFloat(),
                    entity[VisualComponent].region.regionHeight.toFloat()
                ),
                Rectangle(
                    other[LocationComponent].x,
                    other[LocationComponent].y,
                    other[VisualComponent].region.regionWidth.toFloat(),
                    other[VisualComponent].region.regionHeight.toFloat()
                ),
            )
            if (intersects) {
                sequenceOf(entity, other).minBy { it[VisualComponent].region.regionWidth }.remove()
            }
        }
    }
}
