package com.odogwudev.enomatestpractical.dragtoincreaseamount

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.DropShadowPainter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.DropShadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.odogwudev.enomatestpractical.ui.theme.BackgroundLight
import com.odogwudev.enomatestpractical.ui.theme.PurpleIcon
import com.odogwudev.enomatestpractical.ui.theme.PurplePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
private const val STEP_NAIRA = 100 // each tick = ₦100
@Composable
fun NairaAmountIncrement() {
    val dragLimit = 150f
    val scope = rememberCoroutineScope()

    var rawOffsetX by remember { mutableFloatStateOf(0f) }
    val offsetX = remember { Animatable(0f) }
    var count by remember { mutableIntStateOf(0) }
    val leftIconAlpha = remember { Animatable(1f) }
    val rightIconAlpha = remember { Animatable(1f) }

    val direction: Direction by remember {
        derivedStateOf {
            when {
                rawOffsetX >= dragLimit -> Direction.Increase
                rawOffsetX <= -dragLimit -> Direction.Decrease
                else -> Direction.None
            }
        }
    }

    val currentDirection by rememberUpdatedState(newValue = direction)

    LaunchedEffect(currentDirection) {
        if (currentDirection != Direction.None) {
            var delayTime = 500L
            while (isActive) {
                val delta = when (currentDirection) {
                    Direction.Increase -> 1
                    Direction.Decrease -> -1
                    else -> 0
                }
                count = (count + delta).coerceAtLeast(0)  // prevent negatives
                delay(delayTime)
                delayTime = (delayTime * 0.9f).toLong().coerceAtLeast(30L)
            }
        }
    }

    val scaleX by remember {
        derivedStateOf {
            1f + (offsetX.value / dragLimit) * 0.05f
        }
    }
    val nairaIntegerFormatter = remember {
        java.text.NumberFormat.getIntegerInstance(java.util.Locale("en", "NG"))
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            val amount = (count * STEP_NAIRA).coerceAtLeast(0)
            Text(
                text = "₦${nairaIntegerFormatter.format(amount)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(64.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FadeIcon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    alpha = leftIconAlpha.value
                )

                Spacer(modifier = Modifier.width(24.dp))

                StepperBox(
                    count = count,
                    offsetX = offsetX.value,
                    scaleX = scaleX,
                    onDrag = { delta ->
                        val newOffset = (rawOffsetX + delta.x).coerceIn(-dragLimit, dragLimit)
                        rawOffsetX = newOffset

                        scope.launch {
                            offsetX.snapTo(newOffset)
                            if (newOffset > 0) {
                                rightIconAlpha.animateTo(
                                    targetValue = 1f - (newOffset / dragLimit).coerceIn(0f, 1f),
                                    animationSpec = tween(100)
                                )
                            }
                            if (newOffset < 0) {
                                leftIconAlpha.animateTo(
                                    targetValue = 1f - (-newOffset / dragLimit).coerceIn(0f, 1f),
                                    animationSpec = tween(100)
                                )
                            }
                            if (newOffset.absoluteValue < dragLimit / 2) {
                                rightIconAlpha.animateTo(1f, tween(150))
                                leftIconAlpha.animateTo(1f, tween(150))
                            }
                        }
                    },
                    onDragEnd = {
                        rawOffsetX = 0f
                        scope.launch {
                            offsetX.animateTo(
                                0f,
                                spring(
                                    dampingRatio = Spring.DampingRatioHighBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            leftIconAlpha.animateTo(1f, tween(300))
                            rightIconAlpha.animateTo(1f, tween(300))
                        }
                    },
                    animatable = offsetX
                )

                Spacer(modifier = Modifier.width(24.dp))

                FadeIcon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    alpha = rightIconAlpha.value
                )
            }
        }
    }
}

@Composable
private fun StepperBox(
    count: Int,
    offsetX: Float,
    scaleX: Float,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    animatable: Animatable<Float, AnimationVector1D>
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .graphicsLayer(
                scaleX = scaleX * pressScale,
                scaleY = pressScale
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { interactionSource.tryEmit(PressInteraction.Press(it)) },
                    onDragEnd = {
                        interactionSource.tryEmit(
                            PressInteraction.Release(
                                PressInteraction.Press(
                                    Offset.Zero
                                )
                            )
                        )
                        onDragEnd()
                    },
                    onDragCancel = {
                        interactionSource.tryEmit(
                            PressInteraction.Cancel(
                                PressInteraction.Press(
                                    Offset.Zero
                                )
                            )
                        )
                        onDragEnd()
                    },
                    onDrag = { _, dragAmount -> onDrag(dragAmount) }
                )
            }
            .size(120.dp)
            .dropShadow(
                shape = RoundedCornerShape(32.dp),
                dropShadow = DropShadow(15.dp, PurplePrimary, 0.dp, alpha = 0.5f),
                offset = DpOffset(10.dp, 10.dp)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(PurplePrimary),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(250),
                    initialOffsetX = { it / 2 }
                ) + fadeIn(tween(250)) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(250),
                            targetOffsetX = { -it / 2 }
                        ) + fadeOut(tween(250))
            },
            label = "StepperSlide"
        ) { targetCount ->
            Text(
                text = targetCount.toString(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun FadeIcon(
    imageVector: ImageVector,
    alpha: Float
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = PurpleIcon.copy(alpha = alpha),
        modifier = Modifier.size(40.dp)
    )
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