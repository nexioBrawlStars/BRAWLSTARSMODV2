package hu.nexio.hud

import android.content.Context
import hu.nexio.hud.modules.ModuleRegistry
import hu.nexio.hud.modules.ModuleSettings
import hu.nexio.hud.ui.theme.ThemeManager

/** Egyetlen belépési pont: az Activity és az overlay service is ezt hívja. */
object NexioInit {
    fun init(context: Context) {
        val app = context.applicationContext
        HudSettings.init(app)
        ModuleRegistry.init(app)
        ModuleSettings.init(app)
        ThemeManager.init(app)
    }
}
