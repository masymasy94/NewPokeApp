package com.app.pokeapp.presentation.screen

import androidx.lifecycle.ViewModel
import com.app.pokeapp.data.settings.AppSettings
import com.app.pokeapp.domain.model.enums.PokemonType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TypeEffectivenessEditorViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    val typeOverrides: StateFlow<Map<String, Float>> = appSettings.typeOverrides

    fun cycleEffectiveness(attackType: PokemonType, defenseType: PokemonType) {
        val overrideKey = "${attackType.name}->${defenseType.name}"
        val current = appSettings.typeOverrides.value[overrideKey]
            ?: (AppSettings.DEFAULT_EFFECTIVENESS[attackType]?.get(defenseType) ?: 1.0f)

        // Cycle: 1.0 → 2.0 → 0.5 → 0.0 → 1.0
        val next = when (current) {
            1.0f -> 2.0f
            2.0f -> 0.5f
            0.5f -> 0.0f
            0.0f -> 1.0f
            else -> 1.0f
        }
        appSettings.setTypeEffectiveness(attackType, defenseType, next)
    }

    fun resetType(attackType: PokemonType) {
        appSettings.resetTypeOverrides(attackType)
    }

    fun resetAll() {
        appSettings.resetAllTypeOverrides()
    }
}
