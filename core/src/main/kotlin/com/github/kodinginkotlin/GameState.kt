package com.github.kodinginkotlin

object GameState {
    var score: Int = 100
    var lastJumpTime = System.currentTimeMillis()
    var diamondNumber = 0
    val scoreText: String
        get() = "Score: $score; Diamonds: $diamondNumber;"
}
