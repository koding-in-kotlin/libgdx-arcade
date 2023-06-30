package com.github.kodinginkotlin.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.github.kodinginkotlin.component.*
import com.github.kodinginkotlin.component.PlayerDirectionEnum.LEFT
import com.github.kodinginkotlin.component.PlayerDirectionEnum.RIGHT
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.fixture
import ktx.tiled.*

class PhysicsSystem(
    private val physicalWorld: World = inject(),
    map: TiledMap = inject()
) : IntervalSystem() {

    private var testX = 0.0f

    init {
        val entitiesLayer = map.layer("Entities")
        entitiesLayer.objects.forEach {
            if (it is RectangleMapObject){
                physicalWorld.body {
                    position.set(it.x + it.width / 2, it.y + it.height / 2)
                    box(it.width, it.height)
                }
            }
        }

    }

    val family = world.family { all(BodyComponent, LocationComponent) }
    override fun onTick() {
        physicalWorld.step(1 / 12f, 6, 2)
        family.forEach {
            val b = it[BodyComponent].body
            val pos = b.position
            if (PlayerStateComponent in it) {

                val state = it[PlayerStateComponent]
                if (state.directionState == RIGHT) {
                    b.applyLinearImpulse(15000f, 0f, pos.x + 39f, pos.y + 29f, true);
                }
                if (state.directionState == LEFT) {
                    b.applyLinearImpulse(-15000f, 0f, pos.x + 39f, pos.y + 29f, true);
                }

                if (b.linearVelocity.x == 0.0f) {
                    state.state = PlayerStateEnum.IDLE
                }

            }
            // IM: suspish about these two lines, maybe we're missing something
            it[LocationComponent].x = pos.x
            it[LocationComponent].y = pos.y
//            println(b.worldCenter)
            //if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) it[BodyComponent].body.setLinearVelocity(0f, 20f)
//            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
//                b.applyLinearImpulse(testX, 15000f, pos.x + 39f, pos.y + 29f, true);
//            }
//            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
//                b.applyLinearImpulse(testX, -15000f, pos.x+39f, pos.y+29f, true);
//            }
//            if (playerState.stRunnian)
        }
    }
}
