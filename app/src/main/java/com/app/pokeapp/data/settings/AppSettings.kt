package com.app.pokeapp.data.settings

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettings @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _maxPokemonTypes = MutableStateFlow(prefs.getInt(KEY_MAX_TYPES, 2))
    val maxPokemonTypes: StateFlow<Int> = _maxPokemonTypes.asStateFlow()

    fun setMaxPokemonTypes(value: Int) {
        prefs.edit().putInt(KEY_MAX_TYPES, value).apply()
        _maxPokemonTypes.value = value
    }

    companion object {
        private const val KEY_MAX_TYPES = "max_pokemon_types"
    }
}
