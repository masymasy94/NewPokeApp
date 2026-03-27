package com.app.pokeapp.presentation.screen.scanner

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.data.scanner.PokemonMatcher
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.ScanMode
import com.app.pokeapp.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

data class TokenScannerUiState(
    val isInitializing: Boolean = true,
    val isProcessing: Boolean = false,
    val results: List<Pair<Pokemon, Float>> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class TokenScannerViewModel @Inject constructor(
    private val pokemonMatcher: PokemonMatcher,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TokenScannerUiState())
    val uiState: StateFlow<TokenScannerUiState> = _uiState.asStateFlow()

    private var allPokemon: List<Pokemon> = emptyList()

    init {
        viewModelScope.launch {
            allPokemon = pokemonRepository.getAllPokemon().first()
            pokemonMatcher.initialize(allPokemon)
            _uiState.update { it.copy(isInitializing = false) }
        }
    }

    fun processCapture(bitmap: Bitmap, rotationDegrees: Int, scanMode: ScanMode) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isProcessing = true, error = null) }

            val rotated = rotateBitmap(bitmap, rotationDegrees)
            val width = rotated.width
            val height = rotated.height

            val results = when (scanMode) {
                is ScanMode.SingleToken -> {
                    val cropSize = (min(width, height) * 0.6f).toInt()
                    val x = (width - cropSize) / 2
                    val y = (height - cropSize) / 2
                    val crop = Bitmap.createBitmap(rotated, x, y, cropSize, cropSize)
                    val result = pokemonMatcher.findMatch(crop)
                    crop.recycle()
                    listOfNotNull(result?.let { r ->
                        allPokemon.find { it.id == r.pokemonId }?.let { it to r.confidence }
                    })
                }
                is ScanMode.DualToken -> {
                    val cropSize = (min(width, height) * 0.38f).toInt()
                    val y = (height - cropSize) / 2
                    val leftX = (width * 0.05f).toInt()
                    val rightX = (width * 0.55f).toInt()

                    val safeLeftX = leftX.coerceIn(0, width - cropSize)
                    val safeRightX = rightX.coerceIn(0, width - cropSize)
                    val safeY = y.coerceIn(0, height - cropSize)

                    val leftCrop = Bitmap.createBitmap(rotated, safeLeftX, safeY, cropSize, cropSize)
                    val rightCrop = Bitmap.createBitmap(rotated, safeRightX, safeY, cropSize, cropSize)

                    val leftResult = pokemonMatcher.findMatch(leftCrop)
                    val rightResult = pokemonMatcher.findMatch(rightCrop)
                    leftCrop.recycle()
                    rightCrop.recycle()

                    val matches = mutableListOf<Pair<Pokemon, Float>>()
                    leftResult?.let { r ->
                        allPokemon.find { it.id == r.pokemonId }?.let { matches.add(it to r.confidence) }
                    }
                    rightResult?.let { r ->
                        allPokemon.find { it.id == r.pokemonId }?.let { matches.add(it to r.confidence) }
                    }
                    matches
                }
            }

            if (rotated != bitmap) rotated.recycle()

            _uiState.update {
                it.copy(
                    isProcessing = false,
                    results = results,
                    error = if (results.isEmpty()) "Nessun Pok\u00e9mon riconosciuto. Riprova." else null
                )
            }
        }
    }

    fun clearResults() {
        _uiState.update { it.copy(results = emptyList(), error = null) }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
