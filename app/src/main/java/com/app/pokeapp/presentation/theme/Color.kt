package com.app.pokeapp.presentation.theme

import androidx.compose.ui.graphics.Color

fun parseMapColor(mapColor: String?, fallback: Color = Color(0xFFBDBDBD)): Color {
    if (mapColor == null) return fallback
    return when (mapColor.lowercase()) {
        "rosa" -> Color(0xFFE91E63)
        "verde" -> Color(0xFF4CAF50)
        "blu" -> Color(0xFF2196F3)
        else -> try {
            Color(android.graphics.Color.parseColor(mapColor))
        } catch (_: Exception) {
            fallback
        }
    }
}

object PokemonTypes {
    val Normal = Color(0xFFA8A77A)
    val Fire = Color(0xFFEE8130)
    val Water = Color(0xFF6390F0)
    val Electric = Color(0xFFF7D02C)
    val Grass = Color(0xFF7AC74C)
    val Ice = Color(0xFF96D9D6)
    val Fighting = Color(0xFFC22E28)
    val Poison = Color(0xFFA33EA1)
    val Ground = Color(0xFFE2BF65)
    val Flying = Color(0xFFA98FF3)
    val Psychic = Color(0xFFF95587)
    val Bug = Color(0xFFA6B91A)
    val Rock = Color(0xFFB6A136)
    val Ghost = Color(0xFF735797)
    val Dragon = Color(0xFF6F35FC)
    val Steel = Color(0xFFB7B7C0)
    val Fairy = Color(0xFFEE97AC)
    val Dark = Color(0xFF705746)

    val TypeColors = mapOf(
        "NORMAL" to Normal,
        "FIRE" to Fire,
        "WATER" to Water,
        "ELECTRIC" to Electric,
        "GRASS" to Grass,
        "ICE" to Ice,
        "FIGHTING" to Fighting,
        "POISON" to Poison,
        "GROUND" to Ground,
        "FLYING" to Flying,
        "PSYCHIC" to Psychic,
        "BUG" to Bug,
        "ROCK" to Rock,
        "GHOST" to Ghost,
        "DRAGON" to Dragon,
        "STEEL" to Steel,
        "FAIRY" to Fairy,
        "DARK" to Dark
    )
}

object PokemonColors {
    // Primary
    val Primary = Color(0xFFDC0A2D)
    val PrimaryVariant = Color(0xFF9D1D24)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFFFDAD6)
    val OnPrimaryContainer = Color(0xFF410002)
    val PrimaryDark = Color(0xFFFFB4AB)
    val OnPrimaryDark = Color(0xFF690005)
    val PrimaryContainerDark = Color(0xFF8C1D18)
    val OnPrimaryContainerDark = Color(0xFFFFDAD6)

    // Secondary
    val Secondary = Color(0xFF1D3557)
    val SecondaryVariant = Color(0xFF0F1923)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFD4E3FF)
    val OnSecondaryContainer = Color(0xFF0D1B2D)
    val SecondaryContainerDark = Color(0xFF153B64)
    val OnSecondaryContainerDark = Color(0xFFD4E3FF)

    // Tertiary (Pokemon Yellow)
    val Tertiary = Color(0xFF7D5800)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFFFDEAA)
    val OnTertiaryContainer = Color(0xFF271900)
    val TertiaryDark = Color(0xFFF5BF48)
    val OnTertiaryDark = Color(0xFF412D00)
    val TertiaryContainerDark = Color(0xFF5E4200)
    val OnTertiaryContainerDark = Color(0xFFFFDEAA)

    // Backgrounds
    val Background = Color(0xFFFFFBFF)
    val BackgroundDark = Color(0xFF201A1A)
    val OnBackground = Color(0xFF201A1A)
    val OnBackgroundDark = Color(0xFFECE0DF)

    // Surfaces
    val Surface = Color(0xFFFFFBFF)
    val SurfaceDark = Color(0xFF201A1A)
    val OnSurface = Color(0xFF201A1A)
    val OnSurfaceDark = Color(0xFFECE0DF)
    val SurfaceVariant = Color(0xFFF5DDDB)
    val SurfaceVariantDark = Color(0xFF534342)
    val OnSurfaceVariant = Color(0xFF534342)
    val OnSurfaceVariantDark = Color(0xFFD8C2C0)

    // Outline
    val Outline = Color(0xFF857371)
    val OutlineDark = Color(0xFFA08C8B)
    val OutlineVariant = Color(0xFFD8C2C0)
    val OutlineVariantDark = Color(0xFF534342)

    // Error
    val Error = Color(0xFFBA1A1A)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF410002)
    val ErrorDark = Color(0xFFFFB4AB)
    val OnErrorDark = Color(0xFF690005)

    // Semantic
    val Success = Color(0xFF2E7D32)
    val SuccessContainer = Color(0xFFB2F2BB)
    val OnSuccess = Color(0xFFFFFFFF)
    val Warning = Color(0xFFE65100)
    val WarningContainer = Color(0xFFFFDEAA)

    val PokedexRed = Color(0xFFDC0A2D)
    val PokedexRedDark = Color(0xFFB00824)
}
