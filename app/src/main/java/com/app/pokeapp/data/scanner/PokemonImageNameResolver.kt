package com.app.pokeapp.data.scanner

import com.app.pokeapp.domain.model.Pokemon

object PokemonImageNameResolver {
    fun resolve(pokemon: Pokemon): String {
        return when (pokemon.name) {
            "Farfetch'd" -> "farfetchd"
            "MrMime" -> "mr_mime"
            "Nidoran" -> if (pokemon.id == 29) "nidoran_f" else "nidoran_m"
            else -> pokemon.name.lowercase()
        }
    }
}
