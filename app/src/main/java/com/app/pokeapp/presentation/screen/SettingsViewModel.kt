package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import com.app.pokeapp.data.settings.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    val maxTypes: StateFlow<Int> = appSettings.maxPokemonTypes
    val superEffective: StateFlow<Float> = appSettings.superEffectiveMultiplier
    val resistance: StateFlow<Float> = appSettings.resistanceMultiplier

    fun setMaxTypes(value: Int) {
        appSettings.setMaxPokemonTypes(value)
    }

    fun setSuperEffective(value: Float) {
        appSettings.setSuperEffectiveMultiplier(value.coerceIn(1.0f, 3.0f))
    }

    fun setResistance(value: Float) {
        appSettings.setResistanceMultiplier(value.coerceIn(0.0f, 1.0f))
    }
}
