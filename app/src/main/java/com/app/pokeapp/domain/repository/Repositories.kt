package com.app.pokeapp.domain.repository

import com.app.pokeapp.domain.model.Challenger
import com.app.pokeapp.domain.model.ChallengerType
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.RandomEncounter
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getAllPokemon(): Flow<List<Pokemon>>
    fun getPokemonById(id: Int): Flow<Pokemon?>
    fun getPokemonByName(query: String): Flow<List<Pokemon>>
    fun getPokemonByType(type: String): Flow<List<Pokemon>>
}

interface ChallengerRepository {
    fun getAllChallengers(): Flow<List<Challenger>>
    fun getChallengersByType(type: ChallengerType): Flow<List<Challenger>>
    fun getChallengerById(id: Int): Flow<Challenger?>
    fun getRandomEncounter(): Flow<Challenger?>
}

interface RandomEncounterRepository {
    fun getAllEncounters(): Flow<List<RandomEncounter>>
    fun getRandomEncounter(): Flow<RandomEncounter?>
}
