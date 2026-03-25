package com.app.pokeapp.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedLightsBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Extracted to its own composable so that animation-driven recompositions
        // are scoped here and do NOT propagate to content() (the LazyColumn, etc.)
        AnimatedLightsCanvas()
        content()
    }
}

@Composable
private fun AnimatedLightsCanvas() {
    val infiniteTransition = rememberInfiniteTransition(label = "lights")

    val lightAngle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light1"
    )

    val lightAngle2 by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = 480f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light2"
    )

    val lightAngle3 by infiniteTransition.animateFloat(
        initialValue = 240f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light3"
    )

    val lightAngle4 by infiniteTransition.animateFloat(
        initialValue = 60f,
        targetValue = 420f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light4"
    )

    val lightAngle5 by infiniteTransition.animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light5"
    )

    val lightAngle6 by infiniteTransition.animateFloat(
        initialValue = 300f,
        targetValue = 660f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light6"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Red light - upper area
        val rad1 = Math.toRadians(lightAngle1.toDouble())
        val x1 = w * 0.5f + w * 0.25f * cos(rad1).toFloat()
        val y1 = h * 0.3f + h * 0.15f * sin(rad1).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFDC0A2D).copy(alpha = 0.25f),
                    Color(0xFFDC0A2D).copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = Offset(x1, y1),
                radius = w * 0.5f
            ),
            center = Offset(x1, y1),
            radius = w * 0.5f
        )

        // Blue light - middle area
        val rad2 = Math.toRadians(lightAngle2.toDouble())
        val x2 = w * 0.4f + w * 0.3f * cos(rad2).toFloat()
        val y2 = h * 0.55f + h * 0.12f * sin(rad2 * 0.7).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1D3557).copy(alpha = 0.3f),
                    Color(0xFF1D3557).copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(x2, y2),
                radius = w * 0.55f
            ),
            center = Offset(x2, y2),
            radius = w * 0.55f
        )

        // Yellow/gold light - center
        val rad3 = Math.toRadians(lightAngle3.toDouble())
        val x3 = w * 0.6f + w * 0.2f * sin(rad3).toFloat()
        val y3 = h * 0.4f + h * 0.2f * cos(rad3 * 0.5).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF5BF48).copy(alpha = 0.15f),
                    Color(0xFFF5BF48).copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(x3, y3),
                radius = w * 0.45f
            ),
            center = Offset(x3, y3),
            radius = w * 0.45f
        )

        // Purple light - lower left area
        val rad4 = Math.toRadians(lightAngle4.toDouble())
        val x4 = w * 0.3f + w * 0.2f * cos(rad4).toFloat()
        val y4 = h * 0.7f + h * 0.1f * sin(rad4 * 0.8).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF6A1B9A).copy(alpha = 0.2f),
                    Color(0xFF6A1B9A).copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(x4, y4),
                radius = w * 0.5f
            ),
            center = Offset(x4, y4),
            radius = w * 0.5f
        )

        // Red light - lower right area
        val rad5 = Math.toRadians(lightAngle5.toDouble())
        val x5 = w * 0.7f + w * 0.2f * sin(rad5).toFloat()
        val y5 = h * 0.75f + h * 0.1f * cos(rad5 * 0.6).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFDC0A2D).copy(alpha = 0.2f),
                    Color(0xFFDC0A2D).copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(x5, y5),
                radius = w * 0.45f
            ),
            center = Offset(x5, y5),
            radius = w * 0.45f
        )

        // Blue light - bottom center
        val rad6 = Math.toRadians(lightAngle6.toDouble())
        val x6 = w * 0.5f + w * 0.15f * cos(rad6).toFloat()
        val y6 = h * 0.85f + h * 0.08f * sin(rad6 * 0.5).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2A75BB).copy(alpha = 0.2f),
                    Color(0xFF2A75BB).copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(x6, y6),
                radius = w * 0.5f
            ),
            center = Offset(x6, y6),
            radius = w * 0.5f
        )
    }
}
