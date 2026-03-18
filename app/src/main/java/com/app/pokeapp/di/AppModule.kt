package com.app.pokeapp.di

import android.content.Context
import com.app.pokeapp.data.local.AppDatabase
import com.app.pokeapp.data.local.dao.ChallengerDao
import com.app.pokeapp.data.local.dao.PokemonDao
import com.app.pokeapp.data.local.dao.RandomEncounterDao
import com.app.pokeapp.data.repository.ChallengerRepositoryImpl
import com.app.pokeapp.data.repository.PokemonRepositoryImpl
import com.app.pokeapp.data.repository.RandomEncounterRepositoryImpl
import com.app.pokeapp.domain.repository.ChallengerRepository
import com.app.pokeapp.domain.repository.PokemonRepository
import com.app.pokeapp.domain.repository.RandomEncounterRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, scope)
    }

    @Provides
    fun providePokemonDao(database: AppDatabase): PokemonDao {
        return database.pokemonDao()
    }

    @Provides
    fun provideChallengerDao(database: AppDatabase): ChallengerDao {
        return database.challengerDao()
    }

    @Provides
    fun provideRandomEncounterDao(database: AppDatabase): RandomEncounterDao {
        return database.randomEncounterDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPokemonRepository(
        pokemonRepositoryImpl: PokemonRepositoryImpl
    ): PokemonRepository

    @Binds
    @Singleton
    abstract fun bindChallengerRepository(
        challengerRepositoryImpl: ChallengerRepositoryImpl
    ): ChallengerRepository

    @Binds
    @Singleton
    abstract fun bindRandomEncounterRepository(
        randomEncounterRepositoryImpl: RandomEncounterRepositoryImpl
    ): RandomEncounterRepository
}
