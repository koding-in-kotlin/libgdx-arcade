package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Keys.UP
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.github.kodinginkotlin.GameState
import com.github.kodinginkotlin.PPM
import com.github.kodinginkotlin.component.*
import com.github.kodinginkotlin.component.PlayerDirectionEnum.LEFT
import com.github.kodinginkotlin.component.PlayerDirectionEnum.RIGHT
import com.github.kodinginkotlin.getTransformedCenterForRectangle
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.collections.GdxArray
import ktx.tiled.*
import net.dermetfan.gdx.physics.box2d.ContactAdapter


class PhysicsSystem(
    private val physicalWorld: World = inject(),
    map: TiledMap = inject()
) : IntervalSystem() {

    private var testX = 0.0f
    val toRemove = GdxArray<Body>()

    init {
        val entitiesLayer = map.layer("Entities")
        entitiesLayer.objects.forEach {
            if (it is RectangleMapObject) {
                physicalWorld.body {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(it.width / PPM, it.height / PPM)  // WHYYYYYYYYYYYYY
            }.setTransform(it.rectangle.getTransformedCenterForRectangle(), 0f)
            }
        }
        physicalWorld.setContactListener(object : ContactAdapter() {
            override fun beginContact(contact: Contact) {
                println("Collision begin")
                val bodyA = contact.fixtureA.body
                val bodyB = contact.fixtureB.body
                if ((bodyA.type == StaticBody && bodyB.type == DynamicBody) ||
                    (bodyA.type == DynamicBody && bodyB.type == StaticBody)
                ) {
                    val diamond = bodyA.takeIf { it.type == StaticBody } ?: bodyB
                    (diamond.userData as? Entity)?.configure {
                        it.remove()
                        toRemove.add(diamond)
                    }
                }
            }

            override fun endContact(contact: Contact) {
            }
        })

    }


    val family = world.family { all(BodyComponent, LocationComponent).none(DeadComponent) }
    override fun onTick() {
        physicalWorld.step(1 / 12f, 6, 2)
        family.forEach {
            val b = it[BodyComponent].body
            val pos = b.position
            if (PlayerStateComponent in it) {

                val state = it[PlayerStateComponent]
                if (state.state != PlayerStateEnum.IDLE) {
                    if (state.directionState == RIGHT) {
                        b.applyForceToCenter(8f, 0f, true);
                    }
                    if (state.directionState == LEFT) {
                        b.applyForceToCenter(-8f, 0f, true);
                    }

                    if (b.linearVelocity.x == 0.0f) {
                        state.state = PlayerStateEnum.IDLE
                    }
                }

            }
            if (input.isKeyPressed(UP) && b.linearVelocity.y == 0f) b.applyForceToCenter(
                Vector2(0f, 100f),
                true
            )
            it[LocationComponent].x = pos.x
            it[LocationComponent].y = pos.y
        }
        for (body in toRemove) {
            physicalWorld.destroyBody(body)
            GameState.score += 7
        }
        toRemove.clear()
    }
}
