package com.app.pokeapp.data.scanner

import com.app.pokeapp.domain.model.Pokemon
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonTextMatcher @Inject constructor() {

    private var nameLookup: Map<String, Pokemon> = emptyMap()
    var isInitialized: Boolean = false
        private set

    fun initialize(pokemonList: List<Pokemon>) {
        if (isInitialized) return
        nameLookup = buildMap {
            for (pokemon in pokemonList) {
                put(pokemon.name.lowercase(), pokemon)
                put(pokemon.nameItalian.lowercase(), pokemon)
            }
        }
        isInitialized = true
    }

    fun matchText(detectedText: String): Pokemon? {
        if (!isInitialized) return null
        val cleaned = detectedText.trim().lowercase()
        if (cleaned.length < 3) return null

        // Exact match
        nameLookup[cleaned]?.let { return it }

        // Check if any Pokemon name is contained in the detected text
        for ((name, pokemon) in nameLookup) {
            if (name.length >= 4 && cleaned.contains(name)) {
                return pokemon
            }
        }

        // Check if detected text is contained in any Pokemon name (partial OCR)
        if (cleaned.length >= 4) {
            for ((name, pokemon) in nameLookup) {
                if (name.contains(cleaned)) {
                    return pokemon
                }
            }
        }

        return null
    }
}
