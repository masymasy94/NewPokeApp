package com.app.pokeapp.domain.model

import com.app.pokeapp.domain.model.enums.PokemonType

data class Pokemon(
    val id: Int,
    val name: String,
    val nameItalian: String,
    val types: List<PokemonType>,
    val basePower: Int,
    val evolutionStage: Int,
    val preEvolutionId: Int?,
    val postEvolutionId: Int?,
    val move: Move
)

data class Move(
    val name: String,
    val type: PokemonType
)
