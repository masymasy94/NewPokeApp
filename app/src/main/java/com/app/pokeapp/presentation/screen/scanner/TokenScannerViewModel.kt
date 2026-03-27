package com.app.pokeapp.presentation.screen.scanner

import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pokeapp.data.scanner.PokemonTextMatcher
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.ScanMode
import com.app.pokeapp.domain.repository.PokemonRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

data class TokenScannerUiState(
    val isInitializing: Boolean = true,
    val detectedPokemon: List<Pokemon> = emptyList(),
    val isConfirmed: Boolean = false
)

@HiltViewModel
class TokenScannerViewModel @Inject constructor(
    private val pokemonTextMatcher: PokemonTextMatcher,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TokenScannerUiState())
    val uiState: StateFlow<TokenScannerUiState> = _uiState.asStateFlow()

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val isProcessing = AtomicBoolean(false)

    // Stability tracking: confirm after same result N times in a row
    private var stableCount = 0
    private var lastDetectedIds: List<Int> = emptyList()
    private var scanMode: ScanMode = ScanMode.SingleToken

    companion object {
        private const val STABLE_FRAMES_REQUIRED = 4
    }

    init {
        viewModelScope.launch {
            val allPokemon = pokemonRepository.getAllPokemon().first()
            pokemonTextMatcher.initialize(allPokemon)
            _uiState.update { it.copy(isInitializing = false) }
        }
    }

    fun setScanMode(mode: ScanMode) {
        scanMode = mode
    }

    @OptIn(ExperimentalGetImage::class)
    fun processFrame(imageProxy: ImageProxy) {
        if (_uiState.value.isConfirmed) {
            imageProxy.close()
            return
        }

        if (!pokemonTextMatcher.isInitialized || !isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage: Image? = imageProxy.image
        if (mediaImage == null) {
            isProcessing.set(false)
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                handleDetectedText(visionText)
            }
            .addOnCompleteListener {
                isProcessing.set(false)
                imageProxy.close()
            }
    }

    private fun handleDetectedText(visionText: Text) {
        val matches = mutableListOf<Pair<Pokemon, Float>>() // pokemon + x-position

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text
                val pokemon = pokemonTextMatcher.matchText(text)
                if (pokemon != null) {
                    val centerX = line.boundingBox?.centerX()?.toFloat() ?: 0f
                    // Avoid duplicates
                    if (matches.none { it.first.id == pokemon.id }) {
                        matches.add(pokemon to centerX)
                    }
                }
            }
        }

        if (matches.isEmpty()) {
            stableCount = 0
            lastDetectedIds = emptyList()
            _uiState.update { it.copy(detectedPokemon = emptyList()) }
            return
        }

        // Sort by x-position for dual mode (left = player, right = enemy)
        val sorted = matches.sortedBy { it.second }
        val detected = when (scanMode) {
            is ScanMode.SingleToken -> listOf(sorted.first().first)
            is ScanMode.DualToken -> sorted.take(2).map { it.first }
        }

        val expectedCount = when (scanMode) {
            is ScanMode.SingleToken -> 1
            is ScanMode.DualToken -> 2
        }

        _uiState.update { it.copy(detectedPokemon = detected) }

        // Check stability
        val currentIds = detected.map { it.id }
        if (currentIds == lastDetectedIds && detected.size >= expectedCount) {
            stableCount++
            if (stableCount >= STABLE_FRAMES_REQUIRED) {
                _uiState.update { it.copy(isConfirmed = true) }
            }
        } else {
            lastDetectedIds = currentIds
            stableCount = 1
        }
    }

    fun reset() {
        stableCount = 0
        lastDetectedIds = emptyList()
        _uiState.update {
            it.copy(detectedPokemon = emptyList(), isConfirmed = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
    }
}
