package com.app.pokeapp.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.pokeapp.domain.model.enums.PokemonType
import com.app.pokeapp.domain.model.enums.TypeEffectiveness
import com.app.pokeapp.presentation.components.AnimatedLightsBackground
import com.app.pokeapp.presentation.components.TypeBadge
import com.app.pokeapp.presentation.components.TypeBadgeList
import com.app.pokeapp.presentation.theme.PokemonColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onNavigateBack: () -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemon(pokemonId)
    }

    AnimatedLightsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(uiState.pokemon?.nameItalian ?: "Dettagli") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.pokemon != null) {
            val pokemon = uiState.pokemon!!
            val typeColor = pokemon.types.firstOrNull()?.color ?: MaterialTheme.colorScheme.primary

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero header - image only
                val context = LocalContext.current
                val imageName = when (pokemon.name) {
                    "Farfetch'd" -> "farfetchd"
                    "MrMime" -> "mr_mime"
                    "Nidoran" -> if (pokemon.id == 29) "nidoran_f" else "nidoran_m"
                    else -> pokemon.name.lowercase()
                }
                val imageRes = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pokemon image
                    if (imageRes != 0) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = pokemon.nameItalian,
                            modifier = Modifier.size(220.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name
                    Text(
                        text = pokemon.nameItalian,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pokemon types
                    TypeBadgeList(types = pokemon.types)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Move + Power card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Mossa",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = pokemon.move.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TypeBadge(type = pokemon.move.type)
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${pokemon.basePower}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Potenza",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Offensive effectiveness - move type matchups
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Efficacia Mossa",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val moveType = pokemon.move.type
                            val offensiveMatchups = PokemonType.entries
                                .map { it to TypeEffectiveness.getEffectiveness(moveType, it) }
                                .filter { it.second != 1.0f }
                            val superEffective = offensiveMatchups.filter { it.second > 1.0f }
                            val notVeryEffective = offensiveMatchups.filter { it.second < 1.0f && it.second > 0.0f }
                            val noEffect = offensiveMatchups.filter { it.second == 0.0f }

                            if (superEffective.isNotEmpty()) {
                                Text(
                                    text = "Vantaggi",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PokemonColors.Success
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    superEffective.forEach { (type, eff) ->
                                        TypeBadgeWithMultiplier(type = type, multiplier = eff)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (notVeryEffective.isNotEmpty()) {
                                Text(
                                    text = "Svantaggi",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PokemonColors.Warning
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    notVeryEffective.forEach { (type, eff) ->
                                        TypeBadgeWithMultiplier(type = type, multiplier = eff)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (noEffect.isNotEmpty()) {
                                Text(
                                    text = "Danno Nullo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PokemonColors.Error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    noEffect.forEach { (type, _) ->
                                        TypeBadgeWithMultiplier(type = type, multiplier = 0.0f)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Defensive effectiveness - what's strong/weak against this Pokemon
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resistenze e Debolezze",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val defensiveMatchups = PokemonType.entries
                                .map { it to TypeEffectiveness.calculateTotalEffectiveness(it, pokemon.types) }
                                .filter { it.second != 1.0f }
                            val weakTo = defensiveMatchups.filter { it.second > 1.0f }
                            val resistsTo = defensiveMatchups.filter { it.second < 1.0f && it.second > 0.0f }
                            val immuneTo = defensiveMatchups.filter { it.second == 0.0f }

                            if (weakTo.isNotEmpty()) {
                                Text(
                                    text = "Debole contro",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PokemonColors.Error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    weakTo.forEach { (type, eff) ->
                                        TypeBadgeWithMultiplier(type = type, multiplier = eff)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (resistsTo.isNotEmpty()) {
                                Text(
                                    text = "Resiste a",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PokemonColors.Success
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    resistsTo.forEach { (type, eff) ->
                                        TypeBadgeWithMultiplier(type = type, multiplier = eff)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (immuneTo.isNotEmpty()) {
                                Text(
                                    text = "Immune a",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    immuneTo.forEach { (type, _) ->
                                        TypeBadgeWithMultiplier(type = type, multiplier = 0.0f)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: Color
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TypeBadgeWithMultiplier(
    type: PokemonType,
    multiplier: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TypeBadge(type = type)
        val label = if (multiplier == 0.0f) "x0" else "x${multiplier.toBigDecimal().stripTrailingZeros().toPlainString()}"
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

