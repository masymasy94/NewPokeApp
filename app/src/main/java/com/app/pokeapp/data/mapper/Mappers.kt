package com.app.pokeapp.data.mapper

import com.app.pokeapp.data.local.entity.ChallengerEntity
import com.app.pokeapp.data.local.entity.PokemonEntity
import com.app.pokeapp.data.local.entity.RandomEncounterEntity
import com.app.pokeapp.domain.model.Challenger
import com.app.pokeapp.domain.model.ChallengerPokemon
import com.app.pokeapp.domain.model.ChallengerType
import com.app.pokeapp.domain.model.Move
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.RandomEncounter
import com.app.pokeapp.domain.model.enums.PokemonType

fun PokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        nameItalian = nameItalian,
        types = types.split(",").map { PokemonType.fromString(it.trim()) },
        basePower = basePower,
        evolutionStage = evolutionStage,
        preEvolutionId = preEvolutionId,
        postEvolutionId = postEvolutionId,
        move = Move(
            name = moveName,
            type = PokemonType.fromString(moveType)
        )
    )
}

fun ChallengerEntity.toDomain(): Challenger {
    return Challenger(
        id = id,
        name = name,
        type = ChallengerType.valueOf(type),
        pokemons = pokemons.split(",").map { ChallengerPokemon(pokemonId = it.trim().toInt(), level = 5) },
        badgeName = badgeName,
        mapColor = mapColor,
        basePower = basePower,
        firstBonus = firstBonus,
        secondBonus = secondBonus,
        thirdBonus = thirdBonus
    )
}

fun RandomEncounterEntity.toDomain(): RandomEncounter {
    return RandomEncounter(
        id = id,
        trainerName = trainerName,
        pokemonId = pokemonId,
        basePower = basePower,
        mapColor = mapColor
    )
}
