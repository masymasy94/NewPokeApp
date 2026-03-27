package com.app.pokeapp.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.pokeapp.domain.model.ScanMode
import com.app.pokeapp.presentation.components.AnimatedLightsBackground
import com.app.pokeapp.presentation.components.BattleArena
import com.app.pokeapp.presentation.components.EvolutionToggleCard
import com.app.pokeapp.presentation.components.PokemonPickerSheet
import com.app.pokeapp.presentation.screen.scanner.TokenScannerScreen
import com.app.pokeapp.presentation.theme.PokemonColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    onNavigateBack: () -> Unit,
    viewModel: BattleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPlayerPicker by remember { mutableStateOf(false) }
    var showEnemyPicker by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }

    AnimatedLightsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Sfida Allenatori") },
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
                        enemyName = "Allenatore",
                        onPlayerImageClick = { showPlayerPicker = true },
                        onEnemyImageClick = { showEnemyPicker = true },
                        enemyBottomContent = uiState.enemyPokemon?.let { enemyPkm ->
                            {
                                EvolutionToggleCard(
                                    evolutionStage = enemyPkm.evolutionStage,
                                    isFirstEvolution = uiState.isEnemyFirstEvolution,
                                    isSecondEvolution = uiState.isEnemySecondEvolution,
                                    onToggleFirst = { viewModel.toggleEnemyFirstEvolution() },
                                    onToggleSecond = { viewModel.toggleEnemySecondEvolution() }
                                )
                            }
                        },
                        playerBottomContent = uiState.playerPokemon?.let { playerPkm ->
                            {
                                EvolutionToggleCard(
                                    evolutionStage = playerPkm.evolutionStage,
                                    isFirstEvolution = uiState.isFirstEvolution,
                                    isSecondEvolution = uiState.isSecondEvolution,
                                    onToggleFirst = { viewModel.toggleFirstEvolution() },
                                    onToggleSecond = { viewModel.toggleSecondEvolution() }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showScanner = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "\uD83D\uDCF7 Scansiona Gettoni",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (showPlayerPicker) {
                PokemonPickerSheet(
                    pokemonList = uiState.allPokemon,
                    onPokemonSelected = { pokemon ->
                        viewModel.selectPlayerPokemon(pokemon)
                        showPlayerPicker = false
                    },
                    onDismiss = { showPlayerPicker = false }
                )
            }

            if (showEnemyPicker) {
                PokemonPickerSheet(
                    pokemonList = uiState.allPokemon,
                    onPokemonSelected = { pokemon ->
                        viewModel.selectEnemyPokemon(pokemon)
                        showEnemyPicker = false
                    },
                    onDismiss = { showEnemyPicker = false }
                )
            }

            if (showScanner) {
                TokenScannerScreen(
                    scanMode = ScanMode.DualToken,
                    onResult = { scannedPokemon ->
                        if (scannedPokemon.isNotEmpty()) {
                            viewModel.selectPlayerPokemon(scannedPokemon[0])
                        }
                        if (scannedPokemon.size >= 2) {
                            viewModel.selectEnemyPokemon(scannedPokemon[1])
                        }
                        showScanner = false
                    },
                    onDismiss = { showScanner = false }
                )
            }
        }
    }
}
