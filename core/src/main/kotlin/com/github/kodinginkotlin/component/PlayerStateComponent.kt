package com.github.kodinginkotlin.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import com.github.kodinginkotlin.component.PlayerDirectionEnum.RIGHT
import com.github.kodinginkotlin.component.PlayerStateEnum.*
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.assets.disposeSafely
import ktx.collections.toGdxArray

enum class PlayerStateEnum {
    IDLE, RUNNING, ATTACKING
}

enum class PlayerDirectionEnum {
    UP, DOWN, RIGHT, LEFT
}

data class PlayerStateComponent(
    var state: PlayerStateEnum = IDLE,
    var directionState: PlayerDirectionEnum = RIGHT,
    var jumping: Boolean = false,
    var onTheGround: Boolean = false
) : Component<PlayerStateComponent>, Disposable {

    private val disposables = mutableListOf<Disposable>()
    val animation
        get() = when (state) {
            IDLE -> if (directionState == RIGHT) idle else idleLeft
            RUNNING -> if (directionState == RIGHT) run else runLeft
            ATTACKING -> if (directionState == RIGHT) attack else attackLeft
        }
    val idle = idleAnimation()
    val idleLeft = idleAnimation(true)
    val run = runAnimation()
    val runLeft = runAnimation(true)
    val attack = attackAnimation()
    val attackLeft = attackAnimation(true)
    private fun idleAnimation(flip: Boolean = false): Animation<TextureRegion> {
        val texture = Texture(Gdx.files.internal("kings_and_pigs/02-King Pig/Idle (38x28).png")).also(disposables::add)
        val idleFrames =
            TextureRegion.split(texture, texture.width / 12, texture.height)[0]
                .map { it.also { it.flip(!flip, false) } }
                .toGdxArray()
        return Animation(.1f, idleFrames, Animation.PlayMode.LOOP)
    }

    private fun attackAnimation(flip: Boolean = false): Animation<TextureRegion> {
        val texture = Texture(Gdx.files.internal("kings_and_pigs/01-King Human/Attack (78x58).png")).also(disposables::add)
        val attackFrames =
            TextureRegion.split(texture, texture.width / 3, texture.height)[0]
                .map { it.also { it.flip(flip, false) } }
                .toGdxArray()
        return Animation(.05f, attackFrames, Animation.PlayMode.NORMAL)
    }

    private fun runAnimation(flip: Boolean = false): Animation<TextureRegion> {
        val texture = Texture(Gdx.files.internal("kings_and_pigs/02-King Pig/Run (38x28).png")).also(disposables::add)
        val runFrames =
            TextureRegion.split(texture, texture.width / 6, texture.height)[0]
                .map { it.also { it.flip(!flip, false) } }
                .toGdxArray()
        return Animation(.05f, runFrames, Animation.PlayMode.LOOP)
    }

    companion object : ComponentType<PlayerStateComponent>()

    override fun type() = PlayerStateComponent
    override fun dispose() {
        disposables.forEach(Disposable::disposeSafely)
    }
}
