package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val speedX: Float,
    val speedY: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(
    trigger: Boolean,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val progress = remember { Animatable(0f) }
    val colors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF4081), // Pink
        Color(0xFF00E676), // Emerald
        Color(0xFF2979FF), // Blue
        Color(0xFFFF9100), // Orange
        Color(0xFF7C4DFF)  // Purple
    )

    val particles = remember {
        List(45) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                startY = Random.nextFloat() * 0.3f,
                speedX = (Random.nextFloat() - 0.5f) * 1.5f,
                speedY = Random.nextFloat() * 1.2f + 0.8f,
                size = Random.nextFloat() * 14f + 8f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { p ->
            val curX = (p.startX * canvasWidth + p.speedX * t * canvasWidth).mod(canvasWidth)
            val curY = p.startY * canvasHeight + p.speedY * t * canvasHeight
            val alpha = (1f - t).coerceIn(0f, 1f)

            drawRect(
                color = p.color.copy(alpha = alpha),
                topLeft = Offset(curX, curY),
                size = Size(p.size, p.size * 0.7f)
            )
        }
    }
}
