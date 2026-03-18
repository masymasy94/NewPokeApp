package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PokemonDetailUiState(
    val pokemon: Pokemon? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    fun loadPokemon(pokemonId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            pokemonRepository.getPokemonById(pokemonId).collect { pokemon ->
                _uiState.update {
                    it.copy(
                        pokemon = pokemon,
                        isLoading = false
                    )
                }
            }
        }
    }
}
