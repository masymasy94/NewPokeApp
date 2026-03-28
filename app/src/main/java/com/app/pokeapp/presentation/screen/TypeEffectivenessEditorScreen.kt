package com.app.pokeapp.presentation.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.pokeapp.data.settings.AppSettings
import com.app.pokeapp.domain.model.enums.PokemonType
import com.app.pokeapp.presentation.components.AnimatedLightsBackground
import com.app.pokeapp.presentation.components.TypeBadge
import com.app.pokeapp.presentation.theme.PokemonColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeEffectivenessEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: TypeEffectivenessEditorViewModel = hiltViewModel()
) {
    val overrides by viewModel.typeOverrides.collectAsState()
    var selectedAttackType by remember { mutableStateOf<PokemonType?>(null) }

    AnimatedLightsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (selectedAttackType != null)
                                "Attacco: ${selectedAttackType!!.displayName}"
                            else
                                "Tabella Forze/Debolezze"
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (selectedAttackType != null) {
                                selectedAttackType = null
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro"
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            if (selectedAttackType != null) {
                                viewModel.resetType(selectedAttackType!!)
                            } else {
                                viewModel.resetAll()
                            }
                        }) {
                            Text(
                                "Ripristina",
                                color = PokemonColors.Error,
                                fontWeight = FontWeight.Bold
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
            if (selectedAttackType == null) {
                AttackTypeList(
                    overrides = overrides,
                    onTypeSelected = { selectedAttackType = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                )
            } else {
                DefenseMatchupList(
                    attackType = selectedAttackType!!,
                    overrides = overrides,
                    onCycleValue = { defenseType ->
                        viewModel.cycleEffectiveness(selectedAttackType!!, defenseType)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun AttackTypeList(
    overrides: Map<String, Float>,
    onTypeSelected: (PokemonType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(PokemonType.entries.toList()) { type ->
            val hasOverrides = overrides.keys.any { it.startsWith("${type.name}->") }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onTypeSelected(type) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = type.color.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TypeBadge(type = type)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    if (hasOverrides) {
                        Text(
                            text = "Modificato",
                            style = MaterialTheme.typography.labelSmall,
                            color = PokemonColors.Warning
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun DefenseMatchupList(
    attackType: PokemonType,
    overrides: Map<String, Float>,
    onCycleValue: (PokemonType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(PokemonType.entries.toList()) { defenseType ->
            val overrideKey = "${attackType.name}->${defenseType.name}"
            val hasOverride = overrides.containsKey(overrideKey)
            val defaultValue = AppSettings.DEFAULT_EFFECTIVENESS[attackType]?.get(defenseType) ?: 1.0f
            val currentValue = overrides[overrideKey] ?: defaultValue

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { onCycleValue(defenseType) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TypeBadge(type = defenseType)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "vs ${defenseType.displayName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasOverride) {
                            Text(
                                text = "\u2022",
                                color = PokemonColors.Warning,
                                fontSize = 12.sp
                            )
                        }
                        EffectivenessChip(value = currentValue)
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tocca una riga per cambiare il valore",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EffectivenessChip(value: Float) {
    val (text, bgColor) = when (value) {
        2.0f -> "x2" to Color(0xFF2E7D32)
        0.5f -> "x\u00BD" to Color(0xFFB71C1C)
        0.0f -> "x0" to Color(0xFF424242)
        else -> "x1" to Color.White.copy(alpha = 0.15f)
    }
    val textColor = if (value == 1.0f) Color.White.copy(alpha = 0.7f) else Color.White

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
