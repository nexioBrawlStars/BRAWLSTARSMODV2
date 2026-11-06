package hu.nexio.hud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/** Material téma a NEXIO színekkel (az akcentusok a kiválasztott témaprofilból jönnek). */
@Composable
fun NexioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = NexioColors.Purple,
            secondary = NexioColors.Blue,
            tertiary = NexioColors.Cyan,
            background = NexioColors.Background,
            surface = NexioColors.Background,
            onSurface = NexioColors.TextPrimary,
            onBackground = NexioColors.TextPrimary,
        ),
        content = content,
    )
}
