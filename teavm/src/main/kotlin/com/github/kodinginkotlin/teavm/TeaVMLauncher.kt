@file:JvmName("TeaVMLauncher")

package com.github.kodinginkotlin.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.web.WebApplication
import com.github.kodinginkotlin.Main
import com.github.xpenatan.gdx.backends.teavm.TeaApplication

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 1024
        height = 640
    }
    TeaApplication(Main(), config)
}
