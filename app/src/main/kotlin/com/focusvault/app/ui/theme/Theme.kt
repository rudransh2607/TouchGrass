package com.focusvault.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppleLightColorScheme = lightColorScheme(
    primary = AppleIndigo,
    onPrimary = AppleLightCard,
    primaryContainer = AppleIndigoContainer,
    onPrimaryContainer = AppleIndigo,
    secondary = AppleEmerald,
    onSecondary = AppleLightCard,
    secondaryContainer = AppleEmeraldContainer,
    onSecondaryContainer = AppleEmerald,
    background = AppleLightBackground,
    onBackground = AppleTextPrimary,
    surface = AppleLightCard,
    onSurface = AppleTextPrimary,
    surfaceVariant = AppleLightCardElevated,
    onSurfaceVariant = AppleTextSecondary,
    outline = AppleLightBorder,
    outlineVariant = AppleLightDivider,
    error = AppleCoral,
    onError = AppleLightCard,
    errorContainer = AppleCoralContainer,
    onErrorContainer = AppleCoral
)

@Composable
fun FocusVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppleLightColorScheme,
        typography = Typography,
        content = content
    )
}
