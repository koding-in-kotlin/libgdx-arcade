package com.github.kodinginkotlin

import com.badlogic.gdx.math.Vector3
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

object Rumble {
    var rumbleTimeLeft = 0f
        private set
    private var currentTime = 0f
    private var power = 0f
    private var currentPower = 0f
    private var random: Random = Random()
    private val pos: Vector3 = Vector3()
    fun rumble(rumblePower: Float, rumbleLength: Float) {
        power = rumblePower
        rumbleTimeLeft = rumbleLength
        currentTime = 0f
    }

    fun tick(delta: Float): Vector3 {
        if (currentTime <= rumbleTimeLeft) {
            currentPower = power * ((rumbleTimeLeft - currentTime) / rumbleTimeLeft)
            pos.x = (random.nextFloat() - 0.5f) * 2 * currentPower
            pos.y = (random.nextFloat() - 0.5f) * 2 * currentPower
            currentTime += delta
        } else {
            rumbleTimeLeft = 0f
        }
        return pos
    }

    fun getPos(): Vector3 {
        return pos
    }
}
