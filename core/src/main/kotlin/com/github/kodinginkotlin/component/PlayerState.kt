package com.github.kodinginkotlin.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.kodinginkotlin.component.PlayerStateEnum.*
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.collections.toGdxArray
enum class PlayerStateEnum {
    IDLE, RUNNING, ATTACKING
}

data class PlayerState(var movingRight: Boolean = true, var state: PlayerStateEnum = IDLE) : Component<PlayerState> {

    val animation
        get() = when (state) {
            IDLE -> if (movingRight) idle else idleLeft
            RUNNING -> if (movingRight) run else runLeft
            ATTACKING -> if (movingRight) attack else attackLeft
        }

    companion object : ComponentType<PlayerState>() {
        val idle = idleAnimation()
        val idleLeft = idleAnimation(true)
        val run = runAnimation()
        val runLeft = runAnimation(true)
        val attack = attackAnimation()
        val attackLeft = attackAnimation(true)
        private fun idleAnimation(flip: Boolean = false): Animation<TextureRegion> {
            val texture = Texture(Gdx.files.internal("kings_and_pigs/01-King Human/Idle (78x58).png"))
            val idleFrames =
                TextureRegion.split(texture, texture.width / 11, texture.height)[0]
                    .map { it.also { it.flip(flip, false) } }
                    .toGdxArray()
            return Animation(.05f, idleFrames, Animation.PlayMode.LOOP)
        }

        private fun attackAnimation(flip: Boolean = false): Animation<TextureRegion> {
            val texture = Texture(Gdx.files.internal("kings_and_pigs/01-King Human/Attack (78x58).png"))
            val idleFrames =
                TextureRegion.split(texture, texture.width / 3, texture.height)[0]
                    .map { it.also { it.flip(flip, false) } }
                    .toGdxArray()
            return Animation(.05f, idleFrames, Animation.PlayMode.LOOP)
        }

        private fun runAnimation(flip: Boolean = false): Animation<TextureRegion> {
            val texture = Texture(Gdx.files.internal("kings_and_pigs/01-King Human/Run (78x58).png"))
            val idleFrames =
                TextureRegion.split(texture, texture.width / 8, texture.height)[0]
                    .map { it.also { it.flip(flip, false) } }
                    .toGdxArray()
            return Animation(.05f, idleFrames, Animation.PlayMode.LOOP)
        }
    }

    override fun type() = PlayerState
}
