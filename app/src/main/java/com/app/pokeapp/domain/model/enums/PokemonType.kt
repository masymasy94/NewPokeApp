package com.app.pokeapp.domain.model.enums

import androidx.compose.ui.graphics.Color
import com.app.pokeapp.presentation.theme.PokemonTypes

enum class PokemonType(val displayName: String, val color: Color, val onColor: Color) {
    NORMAL("Normal", PokemonTypes.Normal, Color(0xFF3D3D2A)),
    FIRE("Fire", PokemonTypes.Fire, Color(0xFF4A2500)),
    WATER("Water", PokemonTypes.Water, Color(0xFF0D2B5C)),
    ELECTRIC("Electric", PokemonTypes.Electric, Color(0xFF2D2300)),
    GRASS("Grass", PokemonTypes.Grass, Color(0xFF1A3D0E)),
    ICE("Ice", PokemonTypes.Ice, Color(0xFF003330)),
    FIGHTING("Fighting", PokemonTypes.Fighting, Color.White),
    POISON("Poison", PokemonTypes.Poison, Color.White),
    GROUND("Ground", PokemonTypes.Ground, Color(0xFF3D2E00)),
    FLYING("Flying", PokemonTypes.Flying, Color(0xFF2A1B5C)),
    PSYCHIC("Psychic", PokemonTypes.Psychic, Color(0xFF5C0A25)),
    BUG("Bug", PokemonTypes.Bug, Color(0xFF3D4A08)),
    ROCK("Rock", PokemonTypes.Rock, Color(0xFF3D3512)),
    GHOST("Ghost", PokemonTypes.Ghost, Color.White),
    DRAGON("Dragon", PokemonTypes.Dragon, Color.White),
    STEEL("Steel", PokemonTypes.Steel, Color(0xFF2D2D30)),
    FAIRY("Fairy", PokemonTypes.Fairy, Color(0xFF5C1A30)),
    DARK("Dark", PokemonTypes.Dark, Color.White);

    companion object {
        fun fromString(value: String): PokemonType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: NORMAL
        }
    }
}
