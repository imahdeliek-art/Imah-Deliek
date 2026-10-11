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

private val FamilyFinanceColorScheme =
  lightColorScheme(
    primary = CrimsonRed,
    onPrimary = CardBg,
    primaryContainer = SoftPink,
    onPrimaryContainer = DarkCrimson,
    secondary = DarkCrimson,
    onSecondary = CardBg,
    background = AppBg,
    onBackground = TextDark,
    surface = CardBg,
    onSurface = TextDark,
    surfaceVariant = SoftPink,
    onSurfaceVariant = TextMuted,
    outline = BorderPink
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = FamilyFinanceColorScheme,
    typography = Typography,
    content = content
  )
}
