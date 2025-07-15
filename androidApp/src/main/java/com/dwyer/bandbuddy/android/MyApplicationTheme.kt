package com.dwyer.bandbuddy.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF0A84FF),          // iOS Blue
            onPrimary = Color(0xFFFFFFFF),        // White
            primaryContainer = Color(0xFF1E3A8A),  // Darker blue
            onPrimaryContainer = Color(0xFFFFFFFF),
            secondary = Color(0xFF5856D6),        // iOS Purple
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFF4C46A6),
            onSecondaryContainer = Color(0xFFFFFFFF),
            tertiary = Color(0xFF34C759),         // iOS Green
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFF2D7D32),
            onTertiaryContainer = Color(0xFFFFFFFF),
            error = Color(0xFFFF3B30),            // iOS Red
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFD32F2F),
            onErrorContainer = Color(0xFFFFFFFF),
            background = Color(0xFF000000),       // True black
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF1C1C1E),          // iOS Dark Surface
            onSurface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFF2C2C2E),   // iOS Dark Secondary Surface
            onSurfaceVariant = Color(0xFFAEAEB2),  // iOS Light Gray
            outline = Color(0xFF48484A),          // iOS Dark Separator
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF007AFF),          // iOS Blue
            onPrimary = Color(0xFFFFFFFF),        // White
            primaryContainer = Color(0xFFE3F2FD), // Light blue container
            onPrimaryContainer = Color(0xFF0D47A1),
            secondary = Color(0xFF5856D6),        // iOS Purple
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFEDE7F6),
            onSecondaryContainer = Color(0xFF311B92),
            tertiary = Color(0xFF34C759),         // iOS Green
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFE8F5E8),
            onTertiaryContainer = Color(0xFF1B5E20),
            error = Color(0xFFFF3B30),            // iOS Red
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFEBEE),
            onErrorContainer = Color(0xFFB71C1C),
            background = Color(0xFFF2F2F7),       // iOS Light Background
            onBackground = Color(0xFF000000),
            surface = Color(0xFFFFFFFF),          // White surface
            onSurface = Color(0xFF000000),
            surfaceVariant = Color(0xFFF2F2F7),   // iOS Light Secondary Background
            onSurfaceVariant = Color(0xFF8E8E93),  // iOS Secondary Label
            outline = Color(0xFFC7C7CC),          // iOS Light Separator
        )
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),        // More rounded like iOS
        medium = RoundedCornerShape(12.dp),      // iOS-like medium radius
        large = RoundedCornerShape(16.dp)        // iOS-like large radius
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
