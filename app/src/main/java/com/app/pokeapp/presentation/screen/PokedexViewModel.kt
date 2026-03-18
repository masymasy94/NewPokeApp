package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PokedexUiState(
    val pokemonList: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PokedexViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokedexUiState())
    val uiState: StateFlow<PokedexUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadPokemon()
    }

    fun loadPokemon() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            pokemonRepository.getAllPokemon().collect { pokemonList ->
                _uiState.update {
                    it.copy(
                        pokemonList = pokemonList,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                pokemonRepository.getAllPokemon().collect { pokemonList ->
                    _uiState.update { it.copy(pokemonList = pokemonList) }
                }
            } else {
                pokemonRepository.getPokemonByName(query).collect { pokemonList ->
                    _uiState.update { it.copy(pokemonList = pokemonList) }
                }
            }
        }
    }
}
