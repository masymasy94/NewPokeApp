package com.app.pokeapp.domain.model

data class RandomEncounter(
    val id: Int,
    val trainerName: String,
    val pokemonId: Int,
    val basePower: Int,
    val mapColor: String
)
