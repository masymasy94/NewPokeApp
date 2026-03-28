package com.app.pokeapp.domain.model.enums

object TypeEffectiveness {
    // Custom overrides set at runtime from AppSettings
    var customOverrides: Map<String, Float> = emptyMap()

    private val effectivenessMap = mapOf(
        PokemonType.NORMAL to mapOf(
            PokemonType.ROCK to 0.5f,
            PokemonType.GHOST to 0.0f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.FIRE to mapOf(
            PokemonType.FIRE to 0.5f,
            PokemonType.WATER to 0.5f,
            PokemonType.GRASS to 2.0f,
            PokemonType.ICE to 2.0f,
            PokemonType.BUG to 2.0f,
            PokemonType.ROCK to 0.5f,
            PokemonType.DRAGON to 0.5f,
            PokemonType.STEEL to 2.0f
        ),
        PokemonType.WATER to mapOf(
            PokemonType.FIRE to 2.0f,
            PokemonType.WATER to 0.5f,
            PokemonType.GRASS to 0.5f,
            PokemonType.GROUND to 2.0f,
            PokemonType.ROCK to 2.0f,
            PokemonType.DRAGON to 0.5f
        ),
        PokemonType.ELECTRIC to mapOf(
            PokemonType.WATER to 2.0f,
            PokemonType.ELECTRIC to 0.5f,
            PokemonType.GRASS to 0.5f,
            PokemonType.GROUND to 0.0f,
            PokemonType.FLYING to 2.0f,
            PokemonType.DRAGON to 0.5f
        ),
        PokemonType.GRASS to mapOf(
            PokemonType.FIRE to 0.5f,
            PokemonType.WATER to 2.0f,
            PokemonType.GRASS to 0.5f,
            PokemonType.POISON to 0.5f,
            PokemonType.GROUND to 2.0f,
            PokemonType.FLYING to 0.5f,
            PokemonType.BUG to 0.5f,
            PokemonType.ROCK to 2.0f,
            PokemonType.DRAGON to 0.5f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.ICE to mapOf(
            PokemonType.FIRE to 0.5f,
            PokemonType.WATER to 0.5f,
            PokemonType.GRASS to 2.0f,
            PokemonType.ICE to 0.5f,
            PokemonType.GROUND to 2.0f,
            PokemonType.FLYING to 2.0f,
            PokemonType.DRAGON to 2.0f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.FIGHTING to mapOf(
            PokemonType.NORMAL to 2.0f,
            PokemonType.ICE to 2.0f,
            PokemonType.POISON to 0.5f,
            PokemonType.FLYING to 0.5f,
            PokemonType.PSYCHIC to 0.5f,
            PokemonType.BUG to 0.5f,
            PokemonType.ROCK to 2.0f,
            PokemonType.GHOST to 0.0f,
            PokemonType.DARK to 2.0f,
            PokemonType.STEEL to 2.0f,
            PokemonType.FAIRY to 0.5f
        ),
        PokemonType.POISON to mapOf(
            PokemonType.GRASS to 2.0f,
            PokemonType.POISON to 0.5f,
            PokemonType.GROUND to 0.5f,
            PokemonType.ROCK to 0.5f,
            PokemonType.GHOST to 0.5f,
            PokemonType.STEEL to 0.0f,
            PokemonType.FAIRY to 2.0f
        ),
        PokemonType.GROUND to mapOf(
            PokemonType.FIRE to 2.0f,
            PokemonType.ELECTRIC to 2.0f,
            PokemonType.GRASS to 0.5f,
            PokemonType.POISON to 2.0f,
            PokemonType.FLYING to 0.0f,
            PokemonType.BUG to 0.5f,
            PokemonType.ROCK to 2.0f,
            PokemonType.STEEL to 2.0f
        ),
        PokemonType.FLYING to mapOf(
            PokemonType.ELECTRIC to 0.5f,
            PokemonType.GRASS to 2.0f,
            PokemonType.FIGHTING to 2.0f,
            PokemonType.BUG to 2.0f,
            PokemonType.ROCK to 0.5f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.PSYCHIC to mapOf(
            PokemonType.FIGHTING to 2.0f,
            PokemonType.POISON to 2.0f,
            PokemonType.PSYCHIC to 0.5f,
            PokemonType.DARK to 0.0f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.BUG to mapOf(
            PokemonType.FIRE to 0.5f,
            PokemonType.GRASS to 2.0f,
            PokemonType.FIGHTING to 0.5f,
            PokemonType.POISON to 0.5f,
            PokemonType.FLYING to 0.5f,
            PokemonType.PSYCHIC to 2.0f,
            PokemonType.GHOST to 0.5f,
            PokemonType.DARK to 2.0f,
            PokemonType.STEEL to 0.5f,
            PokemonType.FAIRY to 0.5f
        ),
        PokemonType.ROCK to mapOf(
            PokemonType.FIRE to 2.0f,
            PokemonType.ICE to 2.0f,
            PokemonType.FIGHTING to 0.5f,
            PokemonType.GROUND to 0.5f,
            PokemonType.FLYING to 2.0f,
            PokemonType.BUG to 2.0f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.GHOST to mapOf(
            PokemonType.NORMAL to 0.0f,
            PokemonType.PSYCHIC to 2.0f,
            PokemonType.GHOST to 2.0f,
            PokemonType.DARK to 0.5f
        ),
        PokemonType.DRAGON to mapOf(
            PokemonType.DRAGON to 2.0f,
            PokemonType.STEEL to 0.5f,
            PokemonType.FAIRY to 0.0f
        ),
        PokemonType.STEEL to mapOf(
            PokemonType.FIRE to 0.5f,
            PokemonType.WATER to 0.5f,
            PokemonType.ELECTRIC to 0.5f,
            PokemonType.ICE to 2.0f,
            PokemonType.ROCK to 2.0f,
            PokemonType.STEEL to 0.5f,
            PokemonType.FAIRY to 2.0f
        ),
        PokemonType.FAIRY to mapOf(
            PokemonType.FIRE to 0.5f,
            PokemonType.FIGHTING to 2.0f,
            PokemonType.POISON to 0.5f,
            PokemonType.DRAGON to 2.0f,
            PokemonType.DARK to 2.0f,
            PokemonType.STEEL to 0.5f
        ),
        PokemonType.DARK to mapOf(
            PokemonType.FIGHTING to 0.5f,
            PokemonType.PSYCHIC to 2.0f,
            PokemonType.GHOST to 2.0f,
            PokemonType.DARK to 0.5f,
            PokemonType.FAIRY to 0.5f
        )
    )

    fun getRawEffectiveness(attackType: PokemonType, defenseType: PokemonType): Float {
        val overrideKey = "${attackType.name}->${defenseType.name}"
        customOverrides[overrideKey]?.let { return it }
        return effectivenessMap[attackType]?.get(defenseType) ?: 1.0f
    }

    fun getEffectiveness(
        attackType: PokemonType,
        defenseType: PokemonType,
        superEffective: Float = 2.0f,
        notVeryEffective: Float = 0.5f
    ): Float {
        val raw = getRawEffectiveness(attackType, defenseType)
        return when (raw) {
            2.0f -> superEffective
            0.5f -> notVeryEffective
            else -> raw
        }
    }

    fun calculateTotalEffectiveness(
        attackType: PokemonType,
        defenseTypes: List<PokemonType>,
        superEffective: Float = 2.0f,
        notVeryEffective: Float = 0.5f
    ): Float {
        return defenseTypes.fold(1.0f) { acc, type ->
            acc * getEffectiveness(attackType, type, superEffective, notVeryEffective)
        }
    }

    fun getEffectivenessLabel(effectiveness: Float): String {
        return when {
            effectiveness == 0.0f -> "Nessun Effetto"
            effectiveness < 1.0f -> "Poco Efficace"
            effectiveness > 1.0f -> "Super Efficace"
            else -> "Normale"
        }
    }
}
