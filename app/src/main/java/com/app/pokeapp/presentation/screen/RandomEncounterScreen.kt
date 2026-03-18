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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.pokeapp.domain.model.RandomEncounter
import com.app.pokeapp.presentation.components.AnimatedLightsBackground
import com.app.pokeapp.presentation.components.BattleArena
import com.app.pokeapp.presentation.components.PokemonPickerSheet
import com.app.pokeapp.presentation.theme.PokemonColors
import com.app.pokeapp.presentation.theme.parseMapColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomEncounterScreen(
    onNavigateBack: () -> Unit,
    viewModel: RandomEncounterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEnemyPicker by remember { mutableStateOf(false) }
    var showPlayerPicker by remember { mutableStateOf(false) }

    AnimatedLightsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Incontri Casuali") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    BattleArena(
                        playerPokemon = uiState.playerPokemon,
                        enemyPokemon = uiState.enemyPokemon,
                        playerPower = uiState.playerPower,
                        enemyPower = uiState.enemyPower,
                        playerName = "Giocatore",
                        enemyName = uiState.selectedEncounter?.trainerName?.replaceFirstChar { it.uppercase() } ?: "Incontro Casuale",
                        onPlayerImageClick = { showPlayerPicker = true },
                        onEnemyImageClick = { showEnemyPicker = true },
                        enemyExtraContent = {
                            Button(
                                onClick = { showEnemyPicker = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "Seleziona Incontro Casuale",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.startEncounter() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "\uD83C\uDFB2 Casuale",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                }
            }
        }
    }

    if (showEnemyPicker) {
        EncounterPickerSheet(
            encounters = uiState.encounters,
            allPokemon = uiState.allPokemon,
            onEncounterSelected = { encounter ->
                showEnemyPicker = false
                viewModel.startEncounterWithTrainer(encounter)
            },
            onDismiss = { showEnemyPicker = false }
        )
    }

    if (showPlayerPicker) {
        PokemonPickerSheet(
            pokemonList = uiState.allPokemon.filter { it.id <= 129 },
            onPokemonSelected = { pokemon ->
                showPlayerPicker = false
                viewModel.selectSpecificPlayerPokemon(pokemon)
            },
            onDismiss = { showPlayerPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EncounterPickerSheet(
    encounters: List<RandomEncounter>,
    allPokemon: List<com.app.pokeapp.domain.model.Pokemon>,
    onEncounterSelected: (RandomEncounter) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Text(
            text = "Scegli Avversario",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(
                items = encounters,
                key = { it.id }
            ) { encounter ->
                val pokemon = allPokemon.find { it.id == encounter.pokemonId }
                val mapColor = parseMapColor(encounter.mapColor)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEncounterSelected(encounter) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(mapColor, RoundedCornerShape(6.dp))
                        )
                        Column {
                            Text(
                                text = encounter.trainerName.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (pokemon != null) {
                                Text(
                                    text = pokemon.nameItalian,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text = "PWR ${encounter.basePower}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
