package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

val NeonCyberColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = CyberBlack,
  primaryContainer = Color(0xFF0F363F),
  onPrimaryContainer = NeonCyan,
  secondary = NeonViolet,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF3B1E6D),
  onSecondaryContainer = Color(0xFFE9D5FF),
  tertiary = NeonMagenta,
  onTertiary = Color.White,
  background = CyberBlack,
  onBackground = TextPrimary,
  surface = CyberDarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberCardBg,
  onSurfaceVariant = TextSecondary,
  outline = CyberCardBorder,
  error = NeonRed,
  onError = Color.White
)

val AmoledColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF0A2E28),
  onPrimaryContainer = NeonCyan,
  secondary = NeonViolet,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF281140),
  onSecondaryContainer = Color(0xFFDDD6FE),
  tertiary = NeonGreen,
  onTertiary = Color.Black,
  background = AmoledBlack,
  onBackground = TextPrimary,
  surface = AmoledSurface,
  onSurface = TextPrimary,
  surfaceVariant = AmoledCard,
  onSurfaceVariant = TextSecondary,
  outline = AmoledBorder,
  error = NeonRed,
  onError = Color.White
)

val SynthwaveColorScheme = darkColorScheme(
  primary = NeonMagenta,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF4C0E32),
  onPrimaryContainer = Color(0xFFFFD1E8),
  secondary = NeonAmber,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF4D3300),
  onSecondaryContainer = Color(0xFFFFE5A3),
  tertiary = NeonCyan,
  onTertiary = Color.Black,
  background = SynthwaveDark,
  onBackground = TextPrimary,
  surface = SynthwaveSurface,
  onSurface = TextPrimary,
  surfaceVariant = SynthwaveCard,
  onSurfaceVariant = TextSecondary,
  outline = Color(0xFF382963),
  error = NeonRed,
  onError = Color.White
)

@Composable
fun NeonLLMTheme(
  themeMode: ThemeMode = ThemeMode.NEON_CYBER,
  content: @Composable () -> Unit
) {
  val colors = when (themeMode) {
    ThemeMode.NEON_CYBER -> NeonCyberColorScheme
    ThemeMode.AMOLED_PITCH -> AmoledColorScheme
    ThemeMode.SYNTHWAVE_MIDNIGHT -> SynthwaveColorScheme
  }

  MaterialTheme(
    colorScheme = colors,
    typography = Typography,
    content = content
  )
}
