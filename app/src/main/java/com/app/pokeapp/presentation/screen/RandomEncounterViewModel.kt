package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.data.settings.AppSettings
import com.app.pokeapp.domain.model.BattleCalculator
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.RandomEncounter
import com.app.pokeapp.domain.repository.PokemonRepository
import com.app.pokeapp.domain.repository.RandomEncounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RandomEncounterUiState(
    val allPokemon: List<Pokemon> = emptyList(),
    val encounters: List<RandomEncounter> = emptyList(),
    val selectedEncounter: RandomEncounter? = null,
    val playerPokemon: Pokemon? = null,
    val enemyPokemon: Pokemon? = null,
    val playerPower: Int = 0,
    val enemyPower: Int = 0,
    val isFirstEvolution: Boolean = false,
    val isSecondEvolution: Boolean = false,
    val isLoading: Boolean = true,
    val maxTypes: Int = 2
)

@HiltViewModel
class RandomEncounterViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val randomEncounterRepository: RandomEncounterRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(RandomEncounterUiState())
    val uiState: StateFlow<RandomEncounterUiState> = _uiState.asStateFlow()

    init {
        loadData()
        viewModelScope.launch {
            appSettings.maxPokemonTypes.collect { maxTypes ->
                _uiState.update { it.copy(maxTypes = maxTypes) }
                recalculatePowers()
            }
        }
        viewModelScope.launch {
            appSettings.superEffectiveMultiplier.collect { recalculatePowers() }
        }
        viewModelScope.launch {
            appSettings.resistanceMultiplier.collect { recalculatePowers() }
        }
    }

    private fun loadData() {
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
        viewModelScope.launch {
            randomEncounterRepository.getAllEncounters().collect { encounters ->
                _uiState.update { it.copy(encounters = encounters) }
            }
        }
    }

    fun startEncounter() {
        viewModelScope.launch {
            val encounter = randomEncounterRepository.getRandomEncounter().first()
            if (encounter != null) {
                startWithEncounter(encounter)
            }
        }
    }

    fun startEncounterWithTrainer(encounter: RandomEncounter) {
        startWithEncounter(encounter)
    }

    private fun startWithEncounter(encounter: RandomEncounter) {
        val enemyPokemon = _uiState.value.allPokemon.find { it.id == encounter.pokemonId }
        _uiState.update {
            it.copy(
                selectedEncounter = encounter,
                enemyPokemon = enemyPokemon,
                playerPokemon = null,
                playerPower = 0,
                enemyPower = encounter.basePower,
                isFirstEvolution = false,
                isSecondEvolution = false
            )
        }
    }

    fun selectSpecificPlayerPokemon(playerPokemon: Pokemon) {
        _uiState.update {
            it.copy(
                playerPokemon = playerPokemon,
                isFirstEvolution = false,
                isSecondEvolution = false
            )
        }
        recalculatePowers()
    }

    private fun recalculatePowers() {
        val state = _uiState.value
        val player = state.playerPokemon
        val enemy = state.enemyPokemon
        val encounter = state.selectedEncounter
        val maxTypes = appSettings.maxPokemonTypes.value
        val superEff = appSettings.superEffectiveMultiplier.value
        val resistance = appSettings.resistanceMultiplier.value

        if (player != null && enemy != null) {
            val playerPower = BattleCalculator.calculateBattlePower(
                basePower = player.basePower,
                moveType = player.move.type,
                defenderTypes = enemy.types.take(maxTypes),
                isFirstEvolution = state.isFirstEvolution,
                isSecondEvolution = state.isSecondEvolution,
                superEffective = superEff,
                notVeryEffective = resistance
            )
            val enemyPower = BattleCalculator.calculateBattlePower(
                basePower = encounter?.basePower ?: 0,
                moveType = enemy.move.type,
                defenderTypes = player.types.take(maxTypes),
                superEffective = superEff,
                notVeryEffective = resistance
            )
            _uiState.update { it.copy(playerPower = playerPower, enemyPower = enemyPower) }
        }
    }

    fun toggleFirstEvolution() {
        val pokemon = _uiState.value.playerPokemon ?: return
        if (pokemon.evolutionStage <= 1) return
        val newFirst = !_uiState.value.isFirstEvolution
        _uiState.update {
            it.copy(
                isFirstEvolution = newFirst,
                isSecondEvolution = if (!newFirst) false else it.isSecondEvolution
            )
        }
        recalculatePowers()
    }

    fun toggleSecondEvolution() {
        val pokemon = _uiState.value.playerPokemon ?: return
        if (pokemon.evolutionStage < 3) return
        val newSecond = !_uiState.value.isSecondEvolution
        _uiState.update {
            it.copy(
                isSecondEvolution = newSecond,
                isFirstEvolution = if (newSecond) true else it.isFirstEvolution
            )
        }
        recalculatePowers()
    }

    fun reset() {
        _uiState.update {
            it.copy(
                selectedEncounter = null,
                enemyPokemon = null,
                playerPokemon = null,
                playerPower = 0,
                enemyPower = 0,
                isFirstEvolution = false,
                isSecondEvolution = false
            )
        }
    }
}
