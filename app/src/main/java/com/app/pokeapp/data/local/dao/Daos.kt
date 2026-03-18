package com.app.pokeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.pokeapp.data.local.entity.ChallengerEntity
import com.app.pokeapp.data.local.entity.PokemonEntity
import com.app.pokeapp.data.local.entity.RandomEncounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun getAllPokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun getPokemonById(id: Int): Flow<PokemonEntity?>

    @Query("SELECT * FROM pokemon WHERE nameItalian LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchPokemon(query: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE types LIKE '%' || :type || '%'")
    fun getPokemonByType(type: String): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonEntity>)

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun getCount(): Int
}

@Dao
interface ChallengerDao {
    @Query("SELECT * FROM challenger ORDER BY id ASC")
    fun getAllChallengers(): Flow<List<ChallengerEntity>>

    @Query("SELECT * FROM challenger WHERE type = :type")
    fun getChallengersByType(type: String): Flow<List<ChallengerEntity>>

    @Query("SELECT * FROM challenger WHERE id = :id")
    fun getChallengerById(id: Int): Flow<ChallengerEntity?>

    @Query("SELECT * FROM challenger WHERE type = 'RANDOM_ENCOUNTER' ORDER BY RANDOM() LIMIT 1")
    fun getRandomEncounter(): Flow<ChallengerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challengers: List<ChallengerEntity>)

    @Query("SELECT COUNT(*) FROM challenger")
    suspend fun getCount(): Int
}

@Dao
interface RandomEncounterDao {
    @Query("SELECT * FROM random_encounter ORDER BY trainerName ASC")
    fun getAllRandomEncounters(): Flow<List<RandomEncounterEntity>>

    @Query("SELECT * FROM random_encounter ORDER BY RANDOM() LIMIT 1")
    fun getRandomEncounter(): Flow<RandomEncounterEntity?>

    @Query("SELECT * FROM random_encounter WHERE mapColor = :color")
    fun getRandomEncountersByColor(color: String): Flow<List<RandomEncounterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(encounters: List<RandomEncounterEntity>)

    @Query("SELECT COUNT(*) FROM random_encounter")
    suspend fun getCount(): Int
}
