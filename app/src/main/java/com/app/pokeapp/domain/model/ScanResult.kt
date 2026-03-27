package com.app.pokeapp.domain.model

data class ScanResult(
    val pokemonId: Int,
    val confidence: Float
)

sealed class ScanMode {
    data object SingleToken : ScanMode()
    data object DualToken : ScanMode()
}
