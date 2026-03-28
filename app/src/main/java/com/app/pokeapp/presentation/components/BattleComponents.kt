package com.app.pokeapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.pokeapp.R
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.enums.PokemonType
import com.app.pokeapp.presentation.theme.Dimens
import com.app.pokeapp.presentation.theme.PokemonColors

@Composable
fun BattleArena(
    playerPokemon: Pokemon?,
    enemyPokemon: Pokemon?,
    playerPower: Int,
    enemyPower: Int,
    playerName: String = "Giocatore",
    enemyName: String = "Avversario",
    onPlayerImageClick: (() -> Unit)? = null,
    onEnemyImageClick: (() -> Unit)? = null,
    onScanClick: (() -> Unit)? = null,
    onEnemyExtraAction: (() -> Unit)? = null,
    enemyExtraContent: @Composable (() -> Unit)? = null,
    enemyBottomContent: @Composable (() -> Unit)? = null,
    playerBottomContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Enemy half
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box {
                    if (enemyPokemon == null && enemyExtraContent != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            enemyExtraContent()
                        }
                    } else {
                        PokemonBattleSlot(
                            pokemon = enemyPokemon,
                            power = enemyPower,
                            name = enemyName,
                            isPlayer = false,
                            onImageClick = onEnemyImageClick,
                            onExtraAction = onEnemyExtraAction,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            if (enemyBottomContent != null) {
                Spacer(modifier = Modifier.height(4.dp))
                enemyBottomContent()
            }
        }

        // VS badge + scan button
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = PokemonColors.Primary.copy(alpha = 0.5f)
                    )
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PokemonColors.Primary,
                                PokemonColors.PrimaryVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            if (onScanClick != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = PokemonColors.Primary.copy(alpha = 0.5f)
                        )
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    PokemonColors.Primary,
                                    PokemonColors.PrimaryVariant
                                )
                            )
                        )
                        .clickable { onScanClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val w = size.width
                        val h = size.height
                        val color = Color.White
                        val stroke = 2.dp.toPx()
                        // Camera body
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(0f, h * 0.25f),
                            size = androidx.compose.ui.geometry.Size(w, h * 0.65f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                            style = Stroke(stroke)
                        )
                        // Lens circle
                        drawCircle(
                            color = color,
                            radius = w * 0.18f,
                            center = Offset(w * 0.5f, h * 0.58f),
                            style = Stroke(stroke)
                        )
                        // Top bump (flash/viewfinder)
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(w * 0.3f, h * 0.1f),
                            size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.2f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                            style = Stroke(stroke)
                        )
                    }
                }
            }
        }

        // Player half
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF2E7D32).copy(alpha = 0.06f)
                                )
                            )
                        )
                    }
                    PokemonBattleSlot(
                        pokemon = playerPokemon,
                        power = playerPower,
                        name = playerName,
                        isPlayer = true,
                        onImageClick = onPlayerImageClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            if (playerBottomContent != null) {
                Spacer(modifier = Modifier.height(4.dp))
                playerBottomContent()
            }
        }
    }
}

@Composable
private fun PokemonBattleSlot(
    pokemon: Pokemon?,
    power: Int,
    name: String,
    isPlayer: Boolean,
    onImageClick: (() -> Unit)? = null,
    onExtraAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageName = pokemon?.let {
        when (it.name) {
            "Farfetch'd" -> "farfetchd"
            "MrMime" -> "mr_mime"
            "Nidoran" -> if (it.id == 29) "nidoran_f" else "nidoran_m"
            else -> it.name.lowercase()
        }
    } ?: ""
    val imageRes = if (imageName.isNotEmpty()) context.resources.getIdentifier(imageName, "drawable", context.packageName) else 0

    val accentColor = if (isPlayer) PokemonColors.Success else PokemonColors.Error

    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Image section - fixed width to prevent horizontal shifting
        if (pokemon != null) {
            val typeColor = pokemon.types.firstOrNull()?.color ?: MaterialTheme.colorScheme.primary
            Box(modifier = Modifier.width(170.dp), contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(typeColor.copy(alpha = 0.15f))
                        .then(
                            if (onImageClick != null) Modifier.clickable { onImageClick() }
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageRes != 0) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = pokemon.nameItalian,
                            modifier = Modifier
                                .size(142.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (onExtraAction != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            PokemonColors.Primary,
                                            PokemonColors.PrimaryVariant
                                        )
                                    )
                                )
                                .clickable { onExtraAction() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "?",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                    if (onImageClick != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            PokemonColors.Primary,
                                            PokemonColors.PrimaryVariant
                                        )
                                    )
                                )
                                .clickable { onImageClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
                                val stroke = 2.dp.toPx()
                                val color = Color.White
                                val w = size.width
                                val h = size.height
                                drawLine(color, Offset(w * 0.15f, h * 0.3f), Offset(w * 0.85f, h * 0.3f), stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                drawLine(color, Offset(w * 0.65f, h * 0.1f), Offset(w * 0.85f, h * 0.3f), stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                drawLine(color, Offset(w * 0.65f, h * 0.5f), Offset(w * 0.85f, h * 0.3f), stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                drawLine(color, Offset(w * 0.85f, h * 0.7f), Offset(w * 0.15f, h * 0.7f), stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                drawLine(color, Offset(w * 0.35f, h * 0.5f), Offset(w * 0.15f, h * 0.7f), stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                drawLine(color, Offset(w * 0.35f, h * 0.9f), Offset(w * 0.15f, h * 0.7f), stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            }
                        }
                    }
                }
            }
        } else {
            // No pokemon - show pokeball with "Scegli Pokémon" label
            if (onImageClick != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onImageClick() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pokeball_vector),
                        contentDescription = "Scegli Pokémon",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scegli Pokémon",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            return
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info section
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            // Name label
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pokemon.nameItalian,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            TypeBadgeList(types = pokemon.types)

            Spacer(modifier = Modifier.height(8.dp))

            PowerIndicator(power = power, label = "Potenza")
        }
    }
}

@Composable
fun PowerIndicator(
    power: Int,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Text(
            text = "$power",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

@Composable
fun EvolutionToggleCard(
    label: String = "",
    evolutionStage: Int,
    isFirstEvolution: Boolean,
    isSecondEvolution: Boolean,
    onToggleFirst: () -> Unit,
    onToggleSecond: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (evolutionStage <= 1) return

    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isFirstEvolution,
                onCheckedChange = { onToggleFirst() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "1° Evoluzione (+3)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        if (evolutionStage >= 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isSecondEvolution,
                    onCheckedChange = { onToggleSecond() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "2° Evoluzione (+2)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BattleResultCard(
    playerPower: Int,
    enemyPower: Int,
    onRetry: () -> Unit
) {
    val isWin = playerPower > enemyPower
    val isLoss = playerPower < enemyPower
    val resultColor = when {
        isWin -> PokemonColors.Success
        isLoss -> PokemonColors.Error
        else -> PokemonColors.Warning
    }
    val containerColor = when {
        isWin -> PokemonColors.SuccessContainer
        isLoss -> PokemonColors.ErrorContainer
        else -> PokemonColors.WarningContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when {
                    isWin -> "VITTORIA!"
                    isLoss -> "SCONFITTA"
                    else -> "PAREGGIO"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = resultColor,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$playerPower vs $enemyPower",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = resultColor
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Nuova Sfida",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TypeEffectivenessTable(
    defenderType: PokemonType,
    modifier: Modifier = Modifier
) {
    val types = PokemonType.entries.toTypedArray()
    val effectiveTypes = types.filter { defenderType.name != it.name }

    Column(modifier = modifier) {
        Text(
            text = "Efficacia contro $defenderType",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        effectiveTypes.forEach { attackType ->
            val effectiveness = com.app.pokeapp.domain.model.enums.TypeEffectiveness.getEffectiveness(attackType, defenderType)
            if (effectiveness != 1.0f) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TypeBadge(type = attackType)
                    Text(
                        text = when {
                            effectiveness == 0.0f -> "X ${com.app.pokeapp.domain.model.enums.TypeEffectiveness.getEffectivenessLabel(effectiveness)}"
                            effectiveness < 1.0f -> "\u25BC ${com.app.pokeapp.domain.model.enums.TypeEffectiveness.getEffectivenessLabel(effectiveness)}"
                            effectiveness > 1.0f -> "\u25B2 ${com.app.pokeapp.domain.model.enums.TypeEffectiveness.getEffectivenessLabel(effectiveness)}"
                            else -> com.app.pokeapp.domain.model.enums.TypeEffectiveness.getEffectivenessLabel(effectiveness)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            effectiveness > 1.0f -> PokemonColors.Success
                            effectiveness < 1.0f -> PokemonColors.Warning
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = "${effectiveness}x",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
