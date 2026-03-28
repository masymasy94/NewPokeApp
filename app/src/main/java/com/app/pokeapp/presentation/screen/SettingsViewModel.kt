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

    fun setMaxTypes(value: Int) {
        appSettings.setMaxPokemonTypes(value)
    }
}
