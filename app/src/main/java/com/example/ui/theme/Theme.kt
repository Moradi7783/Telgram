package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val HighDensityColorScheme =
  lightColorScheme(
    primary = DensePrimary,
    onPrimary = Color.White,
    primaryContainer = DensePrimaryContainer,
    onPrimaryContainer = DenseOnPrimaryContainer,
    background = DenseBackground,
    onBackground = DenseTextDark,
    surface = DenseSurface,
    onSurface = DenseTextDark,
    surfaceVariant = DenseSurfaceVariant,
    onSurfaceVariant = DenseOnSurfaceVariant
  )

private val DarkHighDensityColorScheme =
  darkColorScheme(
    primary = DensePrimary,
    onPrimary = Color.White,
    primaryContainer = DensePrimaryContainer,
    onPrimaryContainer = DenseOnPrimaryContainer,
    background = DenseBackground,
    onBackground = DenseTextDark,
    surface = DenseSurface,
    onSurface = DenseTextDark,
    surfaceVariant = DenseSurfaceVariant,
    onSurfaceVariant = DenseOnSurfaceVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic system color to preserve the custom hand-crafted Persian High Density branding
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkHighDensityColorScheme else HighDensityColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
