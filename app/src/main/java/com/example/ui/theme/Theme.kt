package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryIndigoLight,
  onPrimary = DarkSurfaceBase,
  primaryContainer = PrimaryIndigoDark,
  onPrimaryContainer = TextPrimaryDark,
  secondary = AccentCyan,
  onSecondary = DarkSurfaceBase,
  secondaryContainer = AccentCyanDark,
  onSecondaryContainer = TextPrimaryDark,
  tertiary = AccentAmber,
  onTertiary = DarkSurfaceBase,
  background = DarkSurfaceBase,
  onBackground = TextPrimaryDark,
  surface = DarkSurfaceElevated,
  onSurface = TextPrimaryDark,
  surfaceVariant = DarkSurfaceCard,
  onSurfaceVariant = TextSecondaryDark,
  outline = DarkSurfaceBorder,
  error = CrimsonAlert,
  onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryIndigo,
  onPrimary = LightSurface,
  primaryContainer = Color(0xFFE0E7FF),
  onPrimaryContainer = PrimaryIndigoDark,
  secondary = AccentCyanDark,
  onSecondary = LightSurface,
  secondaryContainer = Color(0xFFCFFAFE),
  onSecondaryContainer = Color(0xFF164E63),
  tertiary = AccentAmber,
  onTertiary = LightSurface,
  background = LightBackground,
  onBackground = TextPrimaryLight,
  surface = LightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = LightSurfaceCard,
  onSurfaceVariant = TextSecondaryLight,
  outline = LightBorder,
  error = CrimsonAlert,
  onError = LightSurface
)

@Composable
fun EventosTheme(
  darkTheme: Boolean = true, // Default to sleek executive dark mode for command-center look
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window ?: return@SideEffect
      window.statusBarColor = colorScheme.background.toArgb()
      window.navigationBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
