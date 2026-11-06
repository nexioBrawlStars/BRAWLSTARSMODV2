package hu.nexio.hud.modules

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf

enum class ModuleCategory(val label: String) {
    INFO("Info"),
    DISPLAY("Megjelenés"),
    TOOLS("Eszközök"),
}

/**
 * Egy HUD-modul. Új funkció hozzáadása: implementáld ezt az interfészt,
 * majd regisztráld a ModuleRegistry.registerAll()-ban.
 */
interface HudModule {
    val id: String
    val name: String
    val description: String
    val category: ModuleCategory
    val defaultEnabled: Boolean get() = false

    /** A HUD-panelen megjelenő tartalom (csak ha a modul be van kapcsolva). */
    @Composable
    fun Content()
}

/** Modulok nyilvántartása + bekapcsolt állapot mentése (SharedPreferences). */
object ModuleRegistry {
    private const val PREFS = "nexio_modules"
    private val modules = mutableListOf<HudModule>()
    val enabled = mutableStateMapOf<String, Boolean>()

    fun register(module: HudModule) {
        if (modules.none { it.id == module.id }) modules += module
    }

    fun init(context: Context) {
        if (modules.isNotEmpty()) return
        registerAll()
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        modules.forEach { enabled[it.id] = prefs.getBoolean(it.id, it.defaultEnabled) }
    }

    fun setEnabled(context: Context, id: String, value: Boolean) {
        enabled[id] = value
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(id, value).apply()
    }

    fun all(): List<HudModule> = modules
    fun active(): List<HudModule> = modules.filter { enabled[it.id] == true }

    private fun registerAll() {
        register(hu.nexio.hud.modules.builtin.ClockModule)
        register(hu.nexio.hud.modules.builtin.BatteryModule)
        register(hu.nexio.hud.modules.builtin.BrawlTvModule)
        register(hu.nexio.hud.modules.builtin.FpsModule)
        register(hu.nexio.hud.modules.builtin.PingModule)
        register(hu.nexio.hud.modules.builtin.NotesModule)
    }
}
