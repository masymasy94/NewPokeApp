package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.data.settings.AppSettings
import com.app.pokeapp.domain.model.BattleCalculator
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BattleUiState(
    val allPokemon: List<Pokemon> = emptyList(),
    val playerPokemon: Pokemon? = null,
    val enemyPokemon: Pokemon? = null,
    val playerPower: Int = 0,
    val enemyPower: Int = 0,
    val isFirstEvolution: Boolean = false,
    val isSecondEvolution: Boolean = false,
    val isEnemyFirstEvolution: Boolean = false,
    val isEnemySecondEvolution: Boolean = false,
    val isLoading: Boolean = true,
    val maxTypes: Int = 2
)

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(BattleUiState())
    val uiState: StateFlow<BattleUiState> = _uiState.asStateFlow()

    init {
        loadPokemon()
        viewModelScope.launch {
            appSettings.maxPokemonTypes.collect { maxTypes ->
                _uiState.update { it.copy(maxTypes = maxTypes) }
                recalculatePowers()
            }
        }
    }

    private fun loadPokemon() {
        viewModelScope.launch {
            pokemonRepository.getAllPokemon().collect { pokemonList ->
                _uiState.update {
                    it.copy(
                        allPokemon = pokemonList,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectPlayerPokemon(pokemon: Pokemon) {
        _uiState.update {
            it.copy(
                playerPokemon = pokemon,
                isFirstEvolution = false,
                isSecondEvolution = false
            )
        }
        recalculatePowers()
    }

    fun selectEnemyPokemon(pokemon: Pokemon) {
        _uiState.update {
            it.copy(
                enemyPokemon = pokemon,
                isEnemyFirstEvolution = false,
                isEnemySecondEvolution = false
            )
        }
        recalculatePowers()
    }

    private fun recalculatePowers() {
        val state = _uiState.value
        val player = state.playerPokemon
        val enemy = state.enemyPokemon
        val maxTypes = appSettings.maxPokemonTypes.value

        val playerPower = if (player != null) {
            BattleCalculator.calculateBattlePower(
                basePower = player.basePower,
                moveType = player.move.type,
                defenderTypes = enemy?.types?.take(maxTypes),
                isFirstEvolution = state.isFirstEvolution,
                isSecondEvolution = state.isSecondEvolution
            )
        } else 0

        val enemyPower = if (enemy != null) {
            BattleCalculator.calculateBattlePower(
                basePower = enemy.basePower,
                moveType = enemy.move.type,
                defenderTypes = player?.types?.take(maxTypes),
                isFirstEvolution = state.isEnemyFirstEvolution,
                isSecondEvolution = state.isEnemySecondEvolution
            )
        } else 0

        _uiState.update { it.copy(playerPower = playerPower, enemyPower = enemyPower) }
    }

    fun toggleFirstEvolution() {
        val pokemon = _uiState.value.playerPokemon ?: return
        if (pokemon.evolutionStage <= 1) return
        _uiState.update { it.copy(isFirstEvolution = !it.isFirstEvolution) }
        recalculatePowers()
    }

    fun toggleSecondEvolution() {
        val pokemon = _uiState.value.playerPokemon ?: return
        if (pokemon.evolutionStage < 3) return
        _uiState.update { it.copy(isSecondEvolution = !it.isSecondEvolution) }
        recalculatePowers()
    }

    fun toggleEnemyFirstEvolution() {
        val pokemon = _uiState.value.enemyPokemon ?: return
        if (pokemon.evolutionStage <= 1) return
        _uiState.update { it.copy(isEnemyFirstEvolution = !it.isEnemyFirstEvolution) }
        recalculatePowers()
    }

    fun toggleEnemySecondEvolution() {
        val pokemon = _uiState.value.enemyPokemon ?: return
        if (pokemon.evolutionStage < 3) return
        _uiState.update { it.copy(isEnemySecondEvolution = !it.isEnemySecondEvolution) }
        recalculatePowers()
    }

    fun reset() {
        _uiState.update {
            it.copy(
                playerPokemon = null,
                enemyPokemon = null,
                playerPower = 0,
                enemyPower = 0,
                isFirstEvolution = false,
                isSecondEvolution = false,
                isEnemyFirstEvolution = false,
                isEnemySecondEvolution = false
            )
        }
    }
}
