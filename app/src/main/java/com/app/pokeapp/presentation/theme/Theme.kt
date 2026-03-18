package com.app.pokeapp.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PokemonColors.Primary,
    onPrimary = PokemonColors.OnPrimary,
    primaryContainer = PokemonColors.PrimaryContainer,
    onPrimaryContainer = PokemonColors.OnPrimaryContainer,
    secondary = PokemonColors.Secondary,
    onSecondary = PokemonColors.OnSecondary,
    secondaryContainer = PokemonColors.SecondaryContainer,
    onSecondaryContainer = PokemonColors.OnSecondaryContainer,
    tertiary = PokemonColors.Tertiary,
    onTertiary = PokemonColors.OnTertiary,
    tertiaryContainer = PokemonColors.TertiaryContainer,
    onTertiaryContainer = PokemonColors.OnTertiaryContainer,
    background = PokemonColors.Background,
    onBackground = PokemonColors.OnBackground,
    surface = PokemonColors.Surface,
    onSurface = PokemonColors.OnSurface,
    surfaceVariant = PokemonColors.SurfaceVariant,
    onSurfaceVariant = PokemonColors.OnSurfaceVariant,
    outline = PokemonColors.Outline,
    outlineVariant = PokemonColors.OutlineVariant,
    error = PokemonColors.Error,
    onError = PokemonColors.OnError,
    errorContainer = PokemonColors.ErrorContainer,
    onErrorContainer = PokemonColors.OnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = PokemonColors.PrimaryDark,
    onPrimary = PokemonColors.OnPrimaryDark,
    primaryContainer = PokemonColors.PrimaryContainerDark,
    onPrimaryContainer = PokemonColors.OnPrimaryContainerDark,
    secondary = PokemonColors.Secondary,
    onSecondary = PokemonColors.OnSecondary,
    secondaryContainer = PokemonColors.SecondaryContainerDark,
    onSecondaryContainer = PokemonColors.OnSecondaryContainerDark,
    tertiary = PokemonColors.TertiaryDark,
    onTertiary = PokemonColors.OnTertiaryDark,
    tertiaryContainer = PokemonColors.TertiaryContainerDark,
    onTertiaryContainer = PokemonColors.OnTertiaryContainerDark,
    background = PokemonColors.BackgroundDark,
    onBackground = PokemonColors.OnBackgroundDark,
    surface = PokemonColors.SurfaceDark,
    onSurface = PokemonColors.OnSurfaceDark,
    surfaceVariant = PokemonColors.SurfaceVariantDark,
    onSurfaceVariant = PokemonColors.OnSurfaceVariantDark,
    outline = PokemonColors.OutlineDark,
    outlineVariant = PokemonColors.OutlineVariantDark,
    error = PokemonColors.ErrorDark,
    onError = PokemonColors.OnErrorDark,
    errorContainer = PokemonColors.ErrorContainer,
    onErrorContainer = PokemonColors.OnErrorContainer
)

@Composable
fun PokemonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
