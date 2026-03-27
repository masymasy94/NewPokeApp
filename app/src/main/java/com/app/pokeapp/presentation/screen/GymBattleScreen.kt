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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.app.pokeapp.R
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.BorderStroke
import com.app.pokeapp.domain.model.Challenger
import com.app.pokeapp.domain.model.ScanMode
import com.app.pokeapp.presentation.components.AnimatedLightsBackground
import com.app.pokeapp.presentation.components.BattleArena
import com.app.pokeapp.presentation.components.EvolutionToggleCard
import com.app.pokeapp.presentation.components.PokemonPickerSheet
import com.app.pokeapp.presentation.screen.scanner.TokenScannerScreen
import com.app.pokeapp.presentation.theme.PokemonColors
import com.app.pokeapp.presentation.theme.parseMapColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymBattleScreen(
    onNavigateBack: () -> Unit,
    viewModel: GymBattleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPlayerPicker by remember { mutableStateOf(false) }
    var showGymPicker by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }

    AnimatedLightsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Sfida Capopalestra") },
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
                    val selectedGym = uiState.selectedGym
                // Gym info card
                if (selectedGym != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val badgeRes = getBadgeDrawable(selectedGym.badgeName)
                    if (badgeRes != 0) {
                        Image(
                            painter = painterResource(id = badgeRes),
                            contentDescription = selectedGym.badgeName,
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    selectedGym.badgeName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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
                    enemyName = uiState.selectedGym?.name ?: "Capopalestra",
                    onPlayerImageClick = { showPlayerPicker = true },
                    onEnemyImageClick = { showGymPicker = true },
                    enemyExtraContent = {
                        Button(
                            onClick = { showGymPicker = true },
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
                                text = "Scegli Capopalestra",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    enemyBottomContent = if (selectedGym != null) {
                        {
                            DiceBonusSelector(
                                gym = selectedGym,
                                selectedBonus = uiState.selectedDiceBonus,
                                onBonusSelected = { viewModel.selectDiceBonus(it) }
                            )
                        }
                    } else null,
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
                        text = "\uD83D\uDCF7 Scansiona Gettone",
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

        if (showGymPicker) {
            GymPickerSheet(
                gymLeaders = uiState.gymLeaders,
                onGymSelected = { gym ->
                    viewModel.selectGym(gym)
                    showGymPicker = false
                },
                onDismiss = { showGymPicker = false }
            )
        }

        if (showScanner) {
            TokenScannerScreen(
                scanMode = ScanMode.SingleToken,
                onResult = { scannedPokemon ->
                    if (scannedPokemon.isNotEmpty()) {
                        viewModel.selectPlayerPokemon(scannedPokemon[0])
                    }
                    showScanner = false
                },
                onDismiss = { showScanner = false }
            )
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GymPickerSheet(
    gymLeaders: List<Challenger>,
    onGymSelected: (Challenger) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Text(
            text = "Scegli Palestra",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn {
            items(gymLeaders) { gym ->
                val gymColor = parseMapColor(gym.mapColor)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGymSelected(gym) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(gymColor)
                    )

                    Text(
                        text = gym.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    gym.badgeName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun DiceBonusSelector(
    gym: Challenger,
    selectedBonus: Int,
    onBonusSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(gym.firstBonus, gym.secondBonus, gym.thirdBonus).forEach { bonus ->
            val isSelected = selectedBonus == bonus
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onBonusSelected(bonus) }
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onBonusSelected(bonus) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = PokemonColors.Error,
                        unselectedColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                Text(
                    text = "+$bonus",
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun getBadgeDrawable(badgeName: String?): Int {
    return when (badgeName) {
        "Boulder Badge" -> R.drawable.boulder_badge
        "Cascade Badge" -> R.drawable.cascade_badge
        "Thunder Badge" -> R.drawable.thunder_badge
        "Rainbow Badge" -> R.drawable.rainbow_badge
        "Soul Badge" -> R.drawable.soul_badge
        "Marsh Badge" -> R.drawable.marsh_badge
        "Volcano Badge" -> R.drawable.volcano_badge
        "Earth Badge" -> R.drawable.earth_badge
        else -> 0
    }
}
