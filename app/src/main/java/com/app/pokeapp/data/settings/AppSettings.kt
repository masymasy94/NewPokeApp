package com.app.pokeapp.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.app.pokeapp.domain.model.enums.PokemonType
import com.app.pokeapp.domain.model.enums.TypeEffectiveness
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
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

    // Custom type effectiveness overrides
    private val _typeOverrides = MutableStateFlow(loadTypeOverrides())
    val typeOverrides: StateFlow<Map<String, Float>> = _typeOverrides.asStateFlow()

    init {
        // Sync overrides to TypeEffectiveness singleton
        TypeEffectiveness.customOverrides = _typeOverrides.value
    }

    fun setTypeEffectiveness(attackType: PokemonType, defenseType: PokemonType, value: Float) {
        val key = "${attackType.name}->${defenseType.name}"
        val current = _typeOverrides.value.toMutableMap()
        if (value == getDefaultEffectiveness(attackType, defenseType)) {
            current.remove(key)
        } else {
            current[key] = value
        }
        _typeOverrides.value = current
        TypeEffectiveness.customOverrides = current
        saveTypeOverrides(current)
    }

    fun getCustomEffectiveness(attackType: PokemonType, defenseType: PokemonType): Float? {
        return _typeOverrides.value["${attackType.name}->${defenseType.name}"]
    }

    fun resetAllTypeOverrides() {
        _typeOverrides.value = emptyMap()
        TypeEffectiveness.customOverrides = emptyMap()
        prefs.edit().remove(KEY_TYPE_OVERRIDES).apply()
    }

    fun resetTypeOverrides(attackType: PokemonType) {
        val current = _typeOverrides.value.toMutableMap()
        val prefix = "${attackType.name}->"
        current.keys.filter { it.startsWith(prefix) }.forEach { current.remove(it) }
        _typeOverrides.value = current
        TypeEffectiveness.customOverrides = current
        saveTypeOverrides(current)
    }

    private fun getDefaultEffectiveness(attackType: PokemonType, defenseType: PokemonType): Float {
        return DEFAULT_EFFECTIVENESS[attackType]?.get(defenseType) ?: 1.0f
    }

    private fun loadTypeOverrides(): Map<String, Float> {
        val json = prefs.getString(KEY_TYPE_OVERRIDES, null) ?: return emptyMap()
        return try {
            val obj = JSONObject(json)
            buildMap {
                obj.keys().forEach { key ->
                    put(key, obj.getDouble(key).toFloat())
                }
            }
        } catch (_: Exception) { emptyMap() }
    }

    private fun saveTypeOverrides(overrides: Map<String, Float>) {
        val obj = JSONObject()
        overrides.forEach { (k, v) -> obj.put(k, v.toDouble()) }
        prefs.edit().putString(KEY_TYPE_OVERRIDES, obj.toString()).apply()
    }

    companion object {
        private const val KEY_MAX_TYPES = "max_pokemon_types"
        private const val KEY_SUPER_EFFECTIVE = "super_effective_multiplier"
        private const val KEY_RESISTANCE = "resistance_multiplier"
        private const val KEY_TYPE_OVERRIDES = "type_effectiveness_overrides"

        // Copy of default map for reset comparison
        val DEFAULT_EFFECTIVENESS: Map<PokemonType, Map<PokemonType, Float>> = mapOf(
            PokemonType.NORMAL to mapOf(PokemonType.ROCK to 0.5f, PokemonType.GHOST to 0.0f, PokemonType.STEEL to 0.5f),
            PokemonType.FIRE to mapOf(PokemonType.FIRE to 0.5f, PokemonType.WATER to 0.5f, PokemonType.GRASS to 2.0f, PokemonType.ICE to 2.0f, PokemonType.BUG to 2.0f, PokemonType.ROCK to 0.5f, PokemonType.DRAGON to 0.5f, PokemonType.STEEL to 2.0f),
            PokemonType.WATER to mapOf(PokemonType.FIRE to 2.0f, PokemonType.WATER to 0.5f, PokemonType.GRASS to 0.5f, PokemonType.GROUND to 2.0f, PokemonType.ROCK to 2.0f, PokemonType.DRAGON to 0.5f),
            PokemonType.ELECTRIC to mapOf(PokemonType.WATER to 2.0f, PokemonType.ELECTRIC to 0.5f, PokemonType.GRASS to 0.5f, PokemonType.GROUND to 0.0f, PokemonType.FLYING to 2.0f, PokemonType.DRAGON to 0.5f),
            PokemonType.GRASS to mapOf(PokemonType.FIRE to 0.5f, PokemonType.WATER to 2.0f, PokemonType.GRASS to 0.5f, PokemonType.POISON to 0.5f, PokemonType.GROUND to 2.0f, PokemonType.FLYING to 0.5f, PokemonType.BUG to 0.5f, PokemonType.ROCK to 2.0f, PokemonType.DRAGON to 0.5f, PokemonType.STEEL to 0.5f),
            PokemonType.ICE to mapOf(PokemonType.FIRE to 0.5f, PokemonType.WATER to 0.5f, PokemonType.GRASS to 2.0f, PokemonType.ICE to 0.5f, PokemonType.GROUND to 2.0f, PokemonType.FLYING to 2.0f, PokemonType.DRAGON to 2.0f, PokemonType.STEEL to 0.5f),
            PokemonType.FIGHTING to mapOf(PokemonType.NORMAL to 2.0f, PokemonType.ICE to 2.0f, PokemonType.POISON to 0.5f, PokemonType.FLYING to 0.5f, PokemonType.PSYCHIC to 0.5f, PokemonType.BUG to 0.5f, PokemonType.ROCK to 2.0f, PokemonType.GHOST to 0.0f, PokemonType.DARK to 2.0f, PokemonType.STEEL to 2.0f, PokemonType.FAIRY to 0.5f),
            PokemonType.POISON to mapOf(PokemonType.GRASS to 2.0f, PokemonType.POISON to 0.5f, PokemonType.GROUND to 0.5f, PokemonType.ROCK to 0.5f, PokemonType.GHOST to 0.5f, PokemonType.STEEL to 0.0f, PokemonType.FAIRY to 2.0f),
            PokemonType.GROUND to mapOf(PokemonType.FIRE to 2.0f, PokemonType.ELECTRIC to 2.0f, PokemonType.GRASS to 0.5f, PokemonType.POISON to 2.0f, PokemonType.FLYING to 0.0f, PokemonType.BUG to 0.5f, PokemonType.ROCK to 2.0f, PokemonType.STEEL to 2.0f),
            PokemonType.FLYING to mapOf(PokemonType.ELECTRIC to 0.5f, PokemonType.GRASS to 2.0f, PokemonType.FIGHTING to 2.0f, PokemonType.BUG to 2.0f, PokemonType.ROCK to 0.5f, PokemonType.STEEL to 0.5f),
            PokemonType.PSYCHIC to mapOf(PokemonType.FIGHTING to 2.0f, PokemonType.POISON to 2.0f, PokemonType.PSYCHIC to 0.5f, PokemonType.DARK to 0.0f, PokemonType.STEEL to 0.5f),
            PokemonType.BUG to mapOf(PokemonType.FIRE to 0.5f, PokemonType.GRASS to 2.0f, PokemonType.FIGHTING to 0.5f, PokemonType.POISON to 0.5f, PokemonType.FLYING to 0.5f, PokemonType.PSYCHIC to 2.0f, PokemonType.GHOST to 0.5f, PokemonType.DARK to 2.0f, PokemonType.STEEL to 0.5f, PokemonType.FAIRY to 0.5f),
            PokemonType.ROCK to mapOf(PokemonType.FIRE to 2.0f, PokemonType.ICE to 2.0f, PokemonType.FIGHTING to 0.5f, PokemonType.GROUND to 0.5f, PokemonType.FLYING to 2.0f, PokemonType.BUG to 2.0f, PokemonType.STEEL to 0.5f),
            PokemonType.GHOST to mapOf(PokemonType.NORMAL to 0.0f, PokemonType.PSYCHIC to 2.0f, PokemonType.GHOST to 2.0f, PokemonType.DARK to 0.5f),
            PokemonType.DRAGON to mapOf(PokemonType.DRAGON to 2.0f, PokemonType.STEEL to 0.5f, PokemonType.FAIRY to 0.0f),
            PokemonType.STEEL to mapOf(PokemonType.FIRE to 0.5f, PokemonType.WATER to 0.5f, PokemonType.ELECTRIC to 0.5f, PokemonType.ICE to 2.0f, PokemonType.ROCK to 2.0f, PokemonType.STEEL to 0.5f, PokemonType.FAIRY to 2.0f),
            PokemonType.FAIRY to mapOf(PokemonType.FIRE to 0.5f, PokemonType.FIGHTING to 2.0f, PokemonType.POISON to 0.5f, PokemonType.DRAGON to 2.0f, PokemonType.DARK to 2.0f, PokemonType.STEEL to 0.5f),
            PokemonType.DARK to mapOf(PokemonType.FIGHTING to 0.5f, PokemonType.PSYCHIC to 2.0f, PokemonType.GHOST to 2.0f, PokemonType.DARK to 0.5f, PokemonType.FAIRY to 0.5f)
        )
    }
}
