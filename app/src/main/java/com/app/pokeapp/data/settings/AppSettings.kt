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

    private val _superEffectiveMultiplier = MutableStateFlow(prefs.getFloat(KEY_SUPER_EFFECTIVE, 1.5f))
    val superEffectiveMultiplier: StateFlow<Float> = _superEffectiveMultiplier.asStateFlow()

    private val _resistanceMultiplier = MutableStateFlow(prefs.getFloat(KEY_RESISTANCE, 0.75f))
    val resistanceMultiplier: StateFlow<Float> = _resistanceMultiplier.asStateFlow()

    fun setMaxPokemonTypes(value: Int) {
        prefs.edit().putInt(KEY_MAX_TYPES, value).apply()
        _maxPokemonTypes.value = value
    }

    fun setSuperEffectiveMultiplier(value: Float) {
        prefs.edit().putFloat(KEY_SUPER_EFFECTIVE, value).apply()
        _superEffectiveMultiplier.value = value
    }

    fun setResistanceMultiplier(value: Float) {
        prefs.edit().putFloat(KEY_RESISTANCE, value).apply()
        _resistanceMultiplier.value = value
    }

    companion object {
        private const val KEY_MAX_TYPES = "max_pokemon_types"
        private const val KEY_SUPER_EFFECTIVE = "super_effective_multiplier"
        private const val KEY_RESISTANCE = "resistance_multiplier"
    }
}
