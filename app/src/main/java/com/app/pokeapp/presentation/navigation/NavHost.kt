package com.app.pokeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.pokeapp.presentation.screen.CoverScreen
import com.app.pokeapp.presentation.screen.MainMenuScreen
import com.app.pokeapp.presentation.screen.PokedexScreen
import com.app.pokeapp.presentation.screen.PokemonDetailScreen
import com.app.pokeapp.presentation.screen.BattleScreen
import com.app.pokeapp.presentation.screen.GymBattleScreen
import com.app.pokeapp.presentation.screen.RandomEncounterScreen
import com.app.pokeapp.presentation.screen.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Cover.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Cover.route) {
            CoverScreen(
                onCoverClicked = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Cover.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNavigateToPokedex = { navController.navigate(Screen.Pokedex.route) },
                onNavigateToBattle = { navController.navigate(Screen.Battle.route) },
                onNavigateToGymBattle = { navController.navigate(Screen.GymBattle.route) },
                onNavigateToRandomEncounter = { navController.navigate(Screen.RandomEncounter.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Pokedex.route) {
            PokedexScreen(
                onNavigateBack = { navController.popBackStack() },
                onPokemonClick = { pokemonId ->
                    navController.navigate(Screen.PokemonDetail.createRoute(pokemonId))
                }
            )
        }

        composable(
            route = Screen.PokemonDetail.route,
            arguments = listOf(
                navArgument("pokemonId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: 1
            PokemonDetailScreen(
                pokemonId = pokemonId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Battle.route) {
            BattleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GymBattle.route) {
            GymBattleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RandomEncounter.route) {
            RandomEncounterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
