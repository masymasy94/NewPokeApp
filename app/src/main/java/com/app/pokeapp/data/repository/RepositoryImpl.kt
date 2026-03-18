package com.app.pokeapp.data.repository

import com.app.pokeapp.data.local.dao.ChallengerDao
import com.app.pokeapp.data.local.dao.PokemonDao
import com.app.pokeapp.data.local.dao.RandomEncounterDao
import com.app.pokeapp.data.mapper.toDomain
import com.app.pokeapp.domain.model.Challenger
import com.app.pokeapp.domain.model.ChallengerType
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.RandomEncounter
import com.app.pokeapp.domain.repository.ChallengerRepository
import com.app.pokeapp.domain.repository.PokemonRepository
import com.app.pokeapp.domain.repository.RandomEncounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    private val cachedPokemon = MutableStateFlow<List<Pokemon>>(emptyList())
    private var isLoaded = false

    override fun getAllPokemon(): Flow<List<Pokemon>> {
        if (!isLoaded) {
            return pokemonDao.getAllPokemon().map { entities ->
                val list = entities.map { it.toDomain() }
                if (list.isNotEmpty()) {
                    cachedPokemon.value = list
                    isLoaded = true
                }
                list
            }
        }
        return cachedPokemon
    }

    override fun getPokemonById(id: Int): Flow<Pokemon?> {
        if (isLoaded) {
            return MutableStateFlow(cachedPokemon.value.find { it.id == id })
        }
        return pokemonDao.getPokemonById(id).map { it?.toDomain() }
    }

    override fun getPokemonByName(query: String): Flow<List<Pokemon>> {
        if (isLoaded) {
            val lowerQuery = query.lowercase()
            return MutableStateFlow(
                cachedPokemon.value.filter {
                    it.nameItalian.lowercase().contains(lowerQuery) ||
                    it.name.lowercase().contains(lowerQuery)
                }
            )
        }
        return pokemonDao.searchPokemon(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPokemonByType(type: String): Flow<List<Pokemon>> {
        if (isLoaded) {
            return MutableStateFlow(
                cachedPokemon.value.filter { pokemon ->
                    pokemon.types.any { it.name.equals(type, ignoreCase = true) }
                }
            )
        }
        return pokemonDao.getPokemonByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

class ChallengerRepositoryImpl @Inject constructor(
    private val challengerDao: ChallengerDao
) : ChallengerRepository {

    private val cachedChallengers = MutableStateFlow<List<Challenger>>(emptyList())
    private var isLoaded = false

    override fun getAllChallengers(): Flow<List<Challenger>> {
        if (!isLoaded) {
            return challengerDao.getAllChallengers().map { entities ->
                val list = entities.map { it.toDomain() }
                if (list.isNotEmpty()) {
                    cachedChallengers.value = list
                    isLoaded = true
                }
                list
            }
        }
        return cachedChallengers
    }

    override fun getChallengersByType(type: ChallengerType): Flow<List<Challenger>> {
        if (isLoaded) {
            return MutableStateFlow(
                cachedChallengers.value.filter { it.type == type }
            )
        }
        return challengerDao.getChallengersByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getChallengerById(id: Int): Flow<Challenger?> {
        if (isLoaded) {
            return MutableStateFlow(cachedChallengers.value.find { it.id == id })
        }
        return challengerDao.getChallengerById(id).map { it?.toDomain() }
    }

    override fun getRandomEncounter(): Flow<Challenger?> {
        return challengerDao.getRandomEncounter().map { it?.toDomain() }
    }
}

class RandomEncounterRepositoryImpl @Inject constructor(
    private val randomEncounterDao: RandomEncounterDao
) : RandomEncounterRepository {

    override fun getAllEncounters(): Flow<List<RandomEncounter>> {
        return randomEncounterDao.getAllRandomEncounters().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRandomEncounter(): Flow<RandomEncounter?> {
        return randomEncounterDao.getRandomEncounter().map { it?.toDomain() }
    }
}
