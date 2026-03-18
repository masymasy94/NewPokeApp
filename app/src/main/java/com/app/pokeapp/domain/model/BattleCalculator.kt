package com.app.pokeapp.domain.model

import com.app.pokeapp.domain.model.enums.PokemonType
import com.app.pokeapp.domain.model.enums.TypeEffectiveness

object BattleCalculator {

    fun calculateBattlePower(
        basePower: Int,
        moveType: PokemonType,
        defenderTypes: List<PokemonType>?,
        isFirstEvolution: Boolean = false,
        isSecondEvolution: Boolean = false
    ): Int {
        var power = basePower
        if (isFirstEvolution) power += 3
        if (isSecondEvolution) power += 2
        if (defenderTypes != null) {
            val multiplier = TypeEffectiveness.calculateTotalEffectiveness(moveType, defenderTypes)
            power = (power * multiplier).toInt()
        }
        return power
    }
}
