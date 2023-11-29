package com.github.kodinginkotlin.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class DiamondComponent(
    val bobSpread: Float = 0.01f,
    val bobFreq: Float = 10f,
) : Component<DiamondComponent>  {
    companion object : ComponentType<DiamondComponent>()

    override fun type() = DiamondComponent
}
