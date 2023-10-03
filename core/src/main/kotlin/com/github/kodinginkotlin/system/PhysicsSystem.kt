package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.H
import com.badlogic.gdx.Input.Keys.UP
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.github.kodinginkotlin.GameState
import com.github.kodinginkotlin.PPM
import com.github.kodinginkotlin.ShakyCamera
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
import java.lang.Math.abs
import kotlin.math.sign


class PhysicsSystem(
    private val physicalWorld: World = inject(),
    private val camera: ShakyCamera = inject(),
    map: TiledMap = inject()
) : IntervalSystem() {
    private val Body.isPlayer: Boolean
        get() {
            return userData is Entity && PlayerStateComponent in userData as Entity
        }
    private val Body.isWall: Boolean
        get() {
            return userData is Entity && WallComponent in userData as Entity
        }

    val toRemove = GdxArray<Body>()

    init {
        val entitiesLayer = map.layer("Entities")
        entitiesLayer.objects.forEach {
            if (it is RectangleMapObject) {
                val body = physicalWorld.body {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(it.width / PPM, it.height / PPM) {
                        friction = 0.5f
                    }

                }.apply {
                    setTransform(it.rectangle.getTransformedCenterForRectangle(), 0f)
                }
                world.entity {
                    it += WallComponent()
                    body.userData = it
                }
            }

        }
        physicalWorld.setContactListener(object : ContactAdapter() {
            override fun beginContact(contact: Contact) {
                val bodyA = contact.fixtureA.body
                val bodyB = contact.fixtureB.body
                if ((bodyA.type == StaticBody && !bodyA.isWall && bodyB.isPlayer) ||
                    (bodyA.isPlayer && bodyB.type == StaticBody && !bodyB.isWall)
                ) {
                    val diamond = bodyA.takeIf { it.type == StaticBody } ?: bodyB
                    (diamond.userData as? Entity)?.configure {
                        it.remove()
                        toRemove.add(diamond)
//                        camera.shake(amp = 1f/256)
                    }
                }
                if (bodyA.isWall && bodyB.isPlayer && bodyA.position.y < bodyB.position.y ||
                    (bodyB.isWall && bodyA.isPlayer && bodyB.position.y < bodyA.position.y)
                ) {
                    val player = bodyA?.takeIf { it.isPlayer } ?: bodyB
                    (player.userData as Entity)[PlayerStateComponent].onTheGround = true
                }
            }

            override fun endContact(contact: Contact) {
                val bodyA = contact.fixtureA.body
                val bodyB = contact.fixtureB.body
                if (bodyA.isWall && bodyB.isPlayer && bodyA.position.y < bodyB.position.y ||
                    (bodyB.isWall && bodyA.isPlayer && bodyB.position.y < bodyA.position.y)
                ) {
                    val player = bodyA?.takeIf { it.isPlayer } ?: bodyB
                    (player.userData as Entity)[PlayerStateComponent].onTheGround = false
                }

            }
        })

    }

    var capY = 8.0f
    var capYlhs = 0.5f
    var capYrhs = 10f
    var capX = 4.0f
    var capXlhs = 0.5f
    var capXrhs = 10f

    val family = world.family { all(BodyComponent, LocationComponent).none(DeadComponent) }
    override fun onTick() {
        physicalWorld.step(1 / 12f, 6, 2)
        if (input.isKeyJustPressed(Input.Keys.H)) {
            capX -= 0.1f
            capX.coerceAtLeast(capXlhs)
            println("New capX-- ${capX}")
        }
        if (input.isKeyJustPressed(Input.Keys.L)) {
            capX += 0.1f
            capX.coerceAtMost(capXrhs)
            println("New capX++ ${capX}")
        }
        if (input.isKeyJustPressed(Input.Keys.J)) {
            capY -= 0.1f
            capY.coerceAtLeast(capYlhs)
            println("New capY-- ${capY}")
        }
        if (input.isKeyJustPressed(Input.Keys.K)) {
            capY += 0.1f
            capY.coerceAtMost(capYrhs)
            println("New capY++ ${capY}")
        }
        family.forEach {
            val b = it[BodyComponent].body
//            prevVel = b.linearVelocity.y
            val pos = b.position
            if (PlayerStateComponent in it) {
                val state = it[PlayerStateComponent]
                if (state.state != PlayerStateEnum.IDLE) {
                    if (state.directionState == RIGHT && !state.jumping) {
                        b.applyLinearImpulse(10 * deltaTime, 0f, b.position.x, b.position.y, true);
                    }
                    if (state.directionState == LEFT) {
                        b.applyLinearImpulse(-10 * deltaTime, 0f, b.position.x, b.position.y, true);
//                        b.applyForceToCenter(-600*deltaTime, 0f, true);
                    }
                    if (abs(b.linearVelocity.x) >= capX) {
                        val cap = capX * b.linearVelocity.x.sign
                        b.setLinearVelocity(cap, b.linearVelocity.y)
                    }
                    if (b.linearVelocity.x == 0.0f) {
                        state.state = PlayerStateEnum.IDLE
                    }
                }

            }

            if (b.linearVelocity.y >= capY) {
                b.setLinearVelocity(b.linearVelocity.x, capY)
            }

            if (input.isKeyPressed(UP) && b.linearVelocity.y == 0f && PlayerStateComponent in it && it[PlayerStateComponent].onTheGround)
                b.applyLinearImpulse(Vector2(0f, 9f), b.position, true)
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
