package hu.nexio.hud.modules

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Modul- és megjelenési beállítások (SharedPreferences-ben mentve). */
object ModuleSettings {
    private const val PREFS = "nexio_settings"
    private var initialized = false

    var showSimLabel by mutableStateOf(true); private set
    var showTvFeed by mutableStateOf(false); private set
    var rgbAnimated by mutableStateOf(true); private set
    var notes by mutableStateOf(""); private set

    private fun p(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun init(ctx: Context) {
        if (initialized) return
        initialized = true
        val s = p(ctx)
        showSimLabel = s.getBoolean("sim_label", true)
        showTvFeed = s.getBoolean("tv_feed", false)
        rgbAnimated = s.getBoolean("rgb_anim", true)
        notes = s.getString("notes", "") ?: ""
    }

    fun setShowSimLabel(ctx: Context, v: Boolean) { showSimLabel = v; p(ctx).edit().putBoolean("sim_label", v).apply() }
    fun setShowTvFeed(ctx: Context, v: Boolean) { showTvFeed = v; p(ctx).edit().putBoolean("tv_feed", v).apply() }
    fun setRgbAnimated(ctx: Context, v: Boolean) { rgbAnimated = v; p(ctx).edit().putBoolean("rgb_anim", v).apply() }
    fun setNotes(ctx: Context, v: String) { notes = v; p(ctx).edit().putString("notes", v).apply() }
}
