package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.github.kodinginkotlin.*
import com.github.kodinginkotlin.component.*
import com.github.kodinginkotlin.component.PlayerDirectionEnum.LEFT
import com.github.kodinginkotlin.component.PlayerDirectionEnum.RIGHT
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.app.KtxGame
import ktx.app.KtxScreen
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
    map: TiledMap = inject(),
    game: KtxGame<KtxScreen> = inject()
) : IntervalSystem() {
    private val Body.isPlayer: Boolean
        get() {
            return userData is Entity && PlayerStateComponent in userData as Entity
        }
    private val Body.isWall: Boolean
        get() {
            return userData is Entity && WallComponent in userData as Entity
        }
    private val Body.isDiamond: Boolean
        get() {
            return userData is Entity && DiamondComponent in userData as Entity
        }
    private val Body.isExit: Boolean
        get() {
            return userData is Entity && ExitComponent in userData as Entity
        }

    val toRemove = GdxArray<Body>()

    init {
        val entitiesLayer = map.layer("Entities")
        entitiesLayer.objects.forEach {
            if (it is RectangleMapObject) {
                val body = physicalWorld.body {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(it.width / PPM, it.height / PPM) {
                        friction = 0.1f
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
        val exitLayer = map.layer("Exit")
        exitLayer.objects.forEach {
            if (it is RectangleMapObject) {
                val body = physicalWorld.body() {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(it.width / PPM, it.height / PPM) {
                        friction = 0.1f
                        isSensor = true
                    }
                }.apply {
                    setTransform(it.rectangle.getTransformedCenterForRectangle(), 0f)
                }
                world.entity {
                    it += ExitComponent()
                    body.userData = it
                }
            }

        }



        physicalWorld.setContactListener(
            object : ContactAdapter() {
                override fun beginContact(contact: Contact) {
                    val bodyA = contact.fixtureA.body
                    val bodyB = contact.fixtureB.body
                    if ((bodyA.isExit && bodyB.isPlayer) || (bodyB.isExit && bodyA.isPlayer)) {
                        game.setScreen<EndScreen>()
                    }
                    if (bodyA.isDiamond && bodyB.isPlayer || bodyA.isPlayer && bodyB.isDiamond) {
                        val diamond = bodyA.takeIf { it.type == StaticBody } ?: bodyB
                        (diamond.userData as? Entity)?.configure {
                            it.remove()
                            toRemove.add(diamond)
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
                        b.applyLinearImpulse(
                            if (input.isKeyPressed(SHIFT_LEFT)) 0.3f else 2f,
                            0f,
                            b.position.x,
                            b.position.y,
                            true
                        );

                    }
                    if (state.directionState == LEFT) {
                        b.applyLinearImpulse(
                            if (input.isKeyPressed(SHIFT_LEFT)) -0.3f else -2f,
                            0f,
                            b.position.x,
                            b.position.y,
                            true
                        );
//                        b.applyForceToCenter(-600*deltaTime, 0f, true);
                    }
                    if (abs(b.linearVelocity.x) >= capX) {
                        var cap = capX * b.linearVelocity.x.sign
                        if (input.isKeyPressed(SHIFT_LEFT)) {
                            cap /= 4f
                        }
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

            if (input.isKeyPressed(UP)
                && b.linearVelocity.y == 0f
                && PlayerStateComponent in it
                && it[PlayerStateComponent].onTheGround
                && (System.currentTimeMillis() - GameState.lastJumpTime) > 500
            ) {
                b.applyLinearImpulse(Vector2(0f, 4f), b.position, true)
                GameState.lastJumpTime = System.currentTimeMillis()
            }
            it[LocationComponent].x = pos.x
            it[LocationComponent].y = pos.y
        }
        for (body in toRemove) {
            physicalWorld.destroyBody(body)
            GameState.score += 7
            GameState.diamondNumber++
        }
        toRemove.clear()
    }
}
