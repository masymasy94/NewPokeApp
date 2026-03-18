package com.app.pokeapp.presentation.screen

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.app.pokeapp.presentation.components.AnimatedLightsBackground
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private data class MenuItem(
    val title: String,
    val subtitle: String,
    val emoji: String = "",
    val iconRes: Int? = null,
    val gradientColors: List<Color>,
    val onClick: () -> Unit
)

@Composable
fun MainMenuScreen(
    onNavigateToPokedex: () -> Unit,
    onNavigateToBattle: () -> Unit,
    onNavigateToGymBattle: () -> Unit,
    onNavigateToRandomEncounter: () -> Unit
) {
    val menuItems = remember {
        listOf(
            MenuItem(
                title = "Pokedex",
                subtitle = "151 Pokemon",
                iconRes = com.app.pokeapp.R.drawable.pokedex_vector,
                gradientColors = listOf(Color(0xFF43A047), Color(0xFF2E7D32), Color(0xFF1B5E20)),
                onClick = onNavigateToPokedex
            ),
            MenuItem(
                title = "Allenatori",
                subtitle = "Sfida PvP",
                iconRes = com.app.pokeapp.R.drawable.trainers_battle,
                gradientColors = listOf(Color(0xFFFF7043), Color(0xFFE64A19), Color(0xFFBF360C)),
                onClick = onNavigateToBattle
            ),
            MenuItem(
                title = "Capopalestra",
                subtitle = "13 Palestre",
                iconRes = com.app.pokeapp.R.drawable.gym_badges,
                gradientColors = listOf(Color(0xFFAB47BC), Color(0xFF8E24AA), Color(0xFF6A1B9A)),
                onClick = onNavigateToGymBattle
            ),
            MenuItem(
                title = "Incontri casuali",
                subtitle = "Erba alta",
                iconRes = com.app.pokeapp.R.drawable.youngster_joey,
                gradientColors = listOf(Color(0xFFFDD835), Color(0xFFF9A825), Color(0xFFF57F17)),
                onClick = onNavigateToRandomEncounter
            )
        )
    }

    val itemCount = menuItems.size
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Pixels per rotation unit - drag full screen = 2.5 items
    val pixelsPerItem = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    } / 2.5f

    // Scrollable state: converts pixel deltas to rotation
    val scrollableState = rememberScrollableState { delta ->
        rotationAngle -= delta / pixelsPerItem
        delta
    }

    // Fling behavior: decay then snap to nearest item
    val flingBehavior = remember {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                // Fling with decay
                var lastValue = 0f
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity
                ).animateDecay(exponentialDecay(frictionMultiplier = 2f)) {
                    val delta = value - lastValue
                    lastValue = value
                    scrollBy(delta)
                }
                // Snap to nearest item
                val targetAngle = rotationAngle.roundToInt().toFloat()
                val snapDelta = -(targetAngle - rotationAngle) * pixelsPerItem
                lastValue = 0f
                animate(
                    initialValue = 0f,
                    targetValue = snapDelta,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) { value, _ ->
                    val delta = value - lastValue
                    lastValue = value
                    scrollBy(delta)
                }
                return 0f
            }
        }
    }

    AnimatedLightsBackground {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pokeball background decoration
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width * 0.85f
                val centerY = size.height * 0.15f
                val radius = size.width * 0.35f

                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(centerX - radius, centerY),
                    end = Offset(centerX + radius, centerY),
                    strokeWidth = 8f
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = radius * 0.2f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 8f)
                )

                val cx2 = size.width * 0.1f
                val cy2 = size.height * 0.9f
                val r2 = size.width * 0.25f
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = r2,
                    center = Offset(cx2, cy2)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(cx2 - r2, cy2),
                    end = Offset(cx2 + r2, cy2),
                    strokeWidth = 6f
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = r2 * 0.2f,
                    center = Offset(cx2, cy2),
                    style = Stroke(width = 6f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(0.dp))

                // Title section
                Text(
                    text = "PokeApp",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 40.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(3f, 3f),
                            blurRadius = 6f
                        )
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Master Trainer",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 4.sp
                    ),
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp),
                    textAlign = TextAlign.Center
                )

                // 3D circular carousel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .offset(y = (-40).dp)
                        .scrollable(
                            state = scrollableState,
                            orientation = Orientation.Horizontal,
                            flingBehavior = flingBehavior
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val currentRotation = rotationAngle
                    val radiusX = 140f
                    // 30° tilt: vertical radius = radiusX * sin(30°) = radiusX * 0.5
                    val radiusY = radiusX * 0.5f

                    // Position each item on a 30° tilted ring, sort back-to-front
                    val positioned = (0 until itemCount).map { i ->
                        val angle =
                            (i.toFloat() - currentRotation) * (2.0 * PI / itemCount)
                        val z = cos(angle).toFloat()
                        val x = sin(angle).toFloat()
                        // y follows the tilted ring: back items go up
                        val y = z * radiusY
                        data class ItemPos(val index: Int, val x: Float, val z: Float, val y: Float)
                        ItemPos(i, x, z, y)
                    }.sortedBy { it.z }

                    // Subtle floating animation per item
                    val floatTransition = rememberInfiniteTransition(label = "float")
                    val floatOffsets = (0 until itemCount).map { i ->
                        floatTransition.animateFloat(
                            initialValue = -6f,
                            targetValue = 6f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 1800 + i * 400,
                                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "float_$i"
                        )
                    }

                    positioned.forEach { pos ->
                        val item = menuItems[pos.index]
                        val depthFraction = (pos.z + 1f) / 2f
                        val scale = lerp(0.5f, 1f, depthFraction)
                        val alphaVal = lerp(0.3f, 1f, depthFraction)
                        val yOffset = pos.y + floatOffsets[pos.index].value

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (pos.x * radiusX).dp,
                                    y = yOffset.dp
                                )
                                .zIndex(pos.z)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .alpha(alphaVal)
                        ) {
                            MenuCircle(
                                title = item.title,
                                subtitle = item.subtitle,
                                emoji = item.emoji,
                                iconRes = item.iconRes,
                                onClick = item.onClick
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(0.dp))
            }
        }
    }
}

@Composable
private fun MenuCircle(
    title: String,
    subtitle: String,
    emoji: String = "",
    iconRes: Int? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(260.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = emoji,
                    fontSize = 72.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}
