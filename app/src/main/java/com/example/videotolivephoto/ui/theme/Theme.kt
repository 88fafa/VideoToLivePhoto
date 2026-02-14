package com.example.videotolivephoto.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
    secondary = androidx.compose.ui.graphics.Color(0xFF03A9F4),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE1F5FE),
    background = androidx.compose.ui.graphics.Color(0xFFFAFAFA),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF000000),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
)

@Composable
fun VideoToLivePhotoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
