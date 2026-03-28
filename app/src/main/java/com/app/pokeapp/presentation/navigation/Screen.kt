package com.app.pokeapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Cover : Screen("cover")
    data object MainMenu : Screen("main_menu")
    data object Pokedex : Screen("pokedex")
    data object PokemonDetail : Screen("pokemon_detail/{pokemonId}") {
        fun createRoute(pokemonId: Int) = "pokemon_detail/$pokemonId"
    }
    data object Battle : Screen("battle")
    data object GymBattle : Screen("gym_battle")
    data object RandomEncounter : Screen("random_encounter")
    data object Settings : Screen("settings")
    data object TypeEffectivenessEditor : Screen("type_effectiveness_editor")
}
