package hu.nexio.hud.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/** Téma-profilok: négy akcentusszín-készlet. */
enum class ThemeProfile(
    val label: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accent: Color,
) {
    NEXIO("Nexio lila", Color(0xFF9B5CFF), Color(0xFF4D7CFF), Color(0xFF3FD0FF), Color(0xFFC04BFF)),
    OCEAN("Óceán", Color(0xFF2AA9FF), Color(0xFF4D7CFF), Color(0xFF3FFFD0), Color(0xFF7C5CFF)),
    CRIMSON("Bíbor", Color(0xFFFF4D7C), Color(0xFFC04BFF), Color(0xFFFF9B5C), Color(0xFFFF3B5C)),
    TOXIC("Toxic", Color(0xFF4DFF9B), Color(0xFF3FD0FF), Color(0xFFB6FF3F), Color(0xFF9B5CFF)),
}

/** Az aktuális téma; mentve SharedPreferences-be, változáskor a UI újrarajzolódik. */
object ThemeManager {
    private const val PREFS = "nexio_theme"
    private var initialized = false

    var profile by mutableStateOf(ThemeProfile.NEXIO)
        private set

    fun init(ctx: Context) {
        if (initialized) return
        initialized = true
        val name = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("profile", null)
        profile = ThemeProfile.values().firstOrNull { it.name == name } ?: ThemeProfile.NEXIO
    }

    fun set(ctx: Context, p: ThemeProfile) {
        profile = p
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("profile", p.name).apply()
    }
}
