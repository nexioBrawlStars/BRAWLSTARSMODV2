package hu.nexio.hud.ui.theme

import androidx.compose.ui.graphics.Color

/** NEXIO színek: az alap fix, az akcentusok a kiválasztott témaprofilból jönnek. */
object NexioColors {
    val Background = Color(0xFF07060D)
    val Surface = Color(0xCC12101F)
    val TextPrimary = Color(0xFFEDE9FF)
    val TextSecondary = Color(0xFF8C86A8)

    val Purple: Color get() = ThemeManager.profile.primary
    val Blue: Color get() = ThemeManager.profile.secondary
    val Cyan: Color get() = ThemeManager.profile.tertiary
    val Violet: Color get() = ThemeManager.profile.accent
    val SurfaceStroke: Color get() = ThemeManager.profile.primary.copy(alpha = 0.27f)

    /** Az RGB betűstílus színei. */
    val RgbCycle: List<Color>
        get() = ThemeManager.profile.let { listOf(it.primary, it.secondary, it.tertiary, it.secondary, it.primary, it.accent, it.primary) }
}
