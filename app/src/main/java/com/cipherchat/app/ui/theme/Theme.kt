package com.cipherchat.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Cyan = Color(0xFF3EE8CC)
val CyanDim = Color(0xFF159F8C)
val Ink = Color(0xFFE8F6F2)
val Muted = Color(0xFF8EA3AB)
val Bg = Color(0xFF06080C)
val Surface = Color(0xFF0D121A)
val Surface2 = Color(0xFF101820)
val Warn = Color(0xFFF0C35B)
val Danger = Color(0xFFFF8080)
val Ok = Color(0xFF6EF0B4)

private val colors = darkColorScheme(
    primary = Cyan,
    onPrimary = Color(0xFF04221D),
    secondary = CyanDim,
    onSecondary = Ink,
    background = Bg,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    error = Danger,
    onError = Ink
)

@Composable
fun CipherChatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
