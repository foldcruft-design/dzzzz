package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GlassGreyColorScheme = darkColorScheme(
  primary = PrimaryGrey,
  onPrimary = OnPrimaryGrey,
  secondary = SecondaryGrey,
  onSecondary = OnSecondaryGrey,
  tertiary = TertiaryGrey,
  onTertiary = OnPrimaryGrey,
  background = BackgroundDark,
  onBackground = OnSurfaceLight,
  surface = SurfaceDark,
  onSurface = OnSurfaceLight,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = OnSurfaceMuted,
  outline = OutlineGrey
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = GlassGreyColorScheme,
    typography = Typography,
    content = content
  )
}
