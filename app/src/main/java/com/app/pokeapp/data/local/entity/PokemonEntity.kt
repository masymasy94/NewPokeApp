package com.app.pokeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameItalian: String,
    val types: String,
    val basePower: Int,
    val evolutionStage: Int,
    val preEvolutionId: Int?,
    val postEvolutionId: Int?,
    val moveName: String,
    val moveType: String
)

@Entity(tableName = "challenger")
data class ChallengerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val pokemons: String,
    val badgeName: String?,
    val mapColor: String?,
    val basePower: Int = 0,
    val firstBonus: Int = 0,
    val secondBonus: Int = 0,
    val thirdBonus: Int = 0
)

@Entity(tableName = "random_encounter")
data class RandomEncounterEntity(
    @PrimaryKey val id: Int,
    val trainerName: String,
    val pokemonId: Int,
    val basePower: Int,
    val mapColor: String
)
