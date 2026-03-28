package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.data.settings.AppSettings
import com.app.pokeapp.domain.model.BattleCalculator
import com.app.pokeapp.domain.model.Challenger
import com.app.pokeapp.domain.model.ChallengerType
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.repository.ChallengerRepository
import com.app.pokeapp.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GymBattleUiState(
    val allPokemon: List<Pokemon> = emptyList(),
    val gymLeaders: List<Challenger> = emptyList(),
    val selectedGym: Challenger? = null,
    val playerPokemon: Pokemon? = null,
    val enemyPokemon: Pokemon? = null,
    val playerPower: Int = 0,
    val enemyPower: Int = 0,
    val selectedDiceBonus: Int = 0,
    val isFirstEvolution: Boolean = false,
    val isSecondEvolution: Boolean = false,
    val isLoading: Boolean = true,
    val maxTypes: Int = 2
)

@HiltViewModel
class GymBattleViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val challengerRepository: ChallengerRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymBattleUiState())
    val uiState: StateFlow<GymBattleUiState> = _uiState.asStateFlow()

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
                _uiState.update { it.copy(allPokemon = pokemonList) }
            }
        }
        viewModelScope.launch {
            challengerRepository.getChallengersByType(ChallengerType.GYM_LEADER).collect { gymLeaders ->
                _uiState.update {
                    it.copy(
                        gymLeaders = gymLeaders,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectGym(gym: Challenger) {
        _uiState.update { it.copy(selectedGym = gym, selectedDiceBonus = 0) }
        selectEnemyPokemon()
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

    fun selectDiceBonus(bonus: Int) {
        val current = _uiState.value
        val newBonus = if (current.selectedDiceBonus == bonus) 0 else bonus
        _uiState.update { it.copy(selectedDiceBonus = newBonus) }
        recalculatePowers()
    }

    private fun selectEnemyPokemon() {
        val gym = _uiState.value.selectedGym ?: return
        val allPokemon = _uiState.value.allPokemon

        val enemyPokemonId = gym.pokemons.firstOrNull()?.pokemonId
        val enemyPokemon = allPokemon.find { it.id == enemyPokemonId }

        if (enemyPokemon != null) {
            _uiState.update { it.copy(enemyPokemon = enemyPokemon) }
            recalculatePowers()
        }
    }

    private fun recalculatePowers() {
        val state = _uiState.value
        val gym = state.selectedGym ?: return
        val enemyPokemon = state.enemyPokemon
        val playerPokemon = state.playerPokemon
        val maxTypes = appSettings.maxPokemonTypes.value
        val superEff = appSettings.superEffectiveMultiplier.value
        val resistance = appSettings.resistanceMultiplier.value

        val enemyPower = if (enemyPokemon != null) {
            BattleCalculator.calculateBattlePower(
                basePower = gym.basePower + state.selectedDiceBonus,
                moveType = enemyPokemon.move.type,
                defenderTypes = playerPokemon?.types?.take(maxTypes),
                superEffective = superEff,
                notVeryEffective = resistance
            )
        } else 0

        val playerPower = if (playerPokemon != null) {
            BattleCalculator.calculateBattlePower(
                basePower = playerPokemon.basePower,
                moveType = playerPokemon.move.type,
                defenderTypes = enemyPokemon?.types?.take(maxTypes),
                isFirstEvolution = state.isFirstEvolution,
                isSecondEvolution = state.isSecondEvolution,
                superEffective = superEff,
                notVeryEffective = resistance
            )
        } else 0

        _uiState.update { it.copy(playerPower = playerPower, enemyPower = enemyPower) }
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
                playerPokemon = null,
                enemyPokemon = null,
                playerPower = 0,
                enemyPower = 0,
                selectedDiceBonus = 0,
                isFirstEvolution = false,
                isSecondEvolution = false
            )
        }
    }
}
