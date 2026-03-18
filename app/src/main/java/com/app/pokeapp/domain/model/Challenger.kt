package com.app.pokeapp.domain.model

import com.app.pokeapp.domain.model.enums.PokemonType

data class Challenger(
    val id: Int,
    val name: String,
    val type: ChallengerType,
    val pokemons: List<ChallengerPokemon>,
    val badgeName: String? = null,
    val mapColor: String? = null,
    val basePower: Int = 0,
    val firstBonus: Int = 0,
    val secondBonus: Int = 0,
    val thirdBonus: Int = 0
)

enum class ChallengerType {
    TRAINER,
    GYM_LEADER,
    RANDOM_ENCOUNTER
}

data class ChallengerPokemon(
    val pokemonId: Int,
    val level: Int
)
