package com.odogwudev.enomatestpractical.dragtoincreaseamount

class NairaAmountIncrement {
}

sealed class Direction {
    object None : Direction()
    object Increase : Direction()
    object Decrease : Direction()
}
fun calculateScale(offsetX: Float, limit: Float): Float =
    1f + (offsetX / limit) * 0.05f

fun calculateGlowAlpha(count: Int, direction: Direction): Float =
    if (direction != Direction.None) 0.3f + (count % 10) * 0.05f else 0f