package com.github.kodinginkotlin

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import java.util.*

class ShakyCamera(width:Int, height:Int): OrthographicCamera(width.toFloat(), height.toFloat()) {

    private lateinit var samples: FloatArray

    internal var neutralPos : Vector2 = Vector2(0f, 0f)

    private var timer = 0f
    private var duration = 0f

    private var amplitude = 0f
    private var frequency = 0
    private var isFading = true

    public var shake = false

    private fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) +  start

    fun shake(time: Float = 0.1f, amp: Float = 1f, freq: Int = 35, fade: Boolean = true) {
        shake = true
        timer = 0f
        duration = time
        amplitude = amp
        frequency = freq
        isFading = fade
        samples = FloatArray(frequency)
        for (i in 0 until frequency) {
//            samples[i] = Random().nextFloat() * 2f - 1f
            samples[i] = (-1..1).random().toFloat() // only 3 variants (-1, 0, 1) and same visible effect as function above, lol
        }
    }

    fun goBack() {
        position.x = neutralPos.x
        position.y = neutralPos.y
    }

    override fun update() {
        if (shake) {
            if (timer > duration) {
                shake = false
                this.goBack()
            }
            val dt = Gdx.graphics.deltaTime
            timer += dt
            if (duration > 0) {
                duration -= dt
                val shakeTime = timer * frequency
                val first = shakeTime.toInt()
                val second = (first + 1) % frequency
                val third = (first + 2) % frequency
                val deltaT = shakeTime - shakeTime.toInt()
                val deltaX = samples[first] * deltaT + samples[second] * (1f - deltaT)
                val deltaY = samples[second] * deltaT + samples[third] * (1f - deltaT)

                position.x += deltaX * amplitude * if (isFading) duration.coerceAtMost(1f / PPM) else 1f/ PPM
                position.y += deltaY * amplitude * if (isFading) duration.coerceAtMost(1f / PPM) else 1f/ PPM
            }
        }
        super.update()
    }
}

