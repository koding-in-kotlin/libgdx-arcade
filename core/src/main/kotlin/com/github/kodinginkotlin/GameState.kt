package com.github.kodinginkotlin

object GameState {
    var score: Int = 100
    var lastJumpTime = System.currentTimeMillis()
    val scoreText: String
        get() = "Score: $score"
}
