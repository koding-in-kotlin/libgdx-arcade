package com.github.kodinginkotlin.system

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.github.kodinginkotlin.EndScreen
import com.github.kodinginkotlin.ShakyCamera
import com.github.kodinginkotlin.component.DiamondComponent
import com.github.kodinginkotlin.component.ExitComponent
import com.github.kodinginkotlin.component.PlayerStateComponent
import com.github.kodinginkotlin.component.WallComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.collections.GdxArray
import net.dermetfan.gdx.physics.box2d.ContactAdapter

class ContactListener(
    private val game: KtxGame<KtxScreen>,
    private val system: IntervalSystem
) : ContactAdapter(


) {
    val toRemove = GdxArray<Body>()
    private val Body.isPlayer: Boolean
        get() {
            with(system) {
                return userData is Entity && PlayerStateComponent in userData as Entity
            }
        }
    private val Body.isWall: Boolean
        get() {
            with(system) {
                return userData is Entity && WallComponent in userData as Entity
            }
        }
    private val Body.isDiamond: Boolean
        get() {
            with(system) {
                return userData is Entity && DiamondComponent in userData as Entity
            }
        }
    private val Body.isExit: Boolean
        get() {
            with(system) {
                return userData is Entity && ExitComponent in userData as Entity
            }
        }

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body

        if ((bodyA.isExit && bodyB.isPlayer) || (bodyB.isExit && bodyA.isPlayer)) {
            game.setScreen<EndScreen>()
        }
        if (bodyA.isDiamond && bodyB.isPlayer || bodyA.isPlayer && bodyB.isDiamond) {
            val diamond = bodyA.takeIf { it.type == BodyDef.BodyType.StaticBody } ?: bodyB
            with(system) {
                (diamond.userData as? Entity)?.configure {
                    it.remove()
                    toRemove.add(diamond)
                }
            }
        }
        if (bodyA.isWall && bodyB.isPlayer && bodyA.position.y < bodyB.position.y ||
            (bodyB.isWall && bodyA.isPlayer && bodyB.position.y < bodyA.position.y)
        ) {
            val player = bodyA?.takeIf { it.isPlayer } ?: bodyB
            with(system) {
                (player.userData as Entity)[PlayerStateComponent].onTheGround = true
            }
        }

    }

    override fun endContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body
        if (bodyA.isWall && bodyB.isPlayer && bodyA.position.y < bodyB.position.y ||
            (bodyB.isWall && bodyA.isPlayer && bodyB.position.y < bodyA.position.y)
        ) {
            val player = bodyA?.takeIf { it.isPlayer } ?: bodyB
            with(system) {
                (player.userData as Entity)[PlayerStateComponent].onTheGround = false
            }
        }

    }
}
