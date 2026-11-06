package hu.nexio.hud.modules.builtin

import android.view.Choreographer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import hu.nexio.hud.modules.HudModule
import hu.nexio.hud.modules.ModuleCategory
import hu.nexio.hud.modules.ModuleSettings
import hu.nexio.hud.ui.theme.NexioColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

private val toolText get() = TextStyle(color = NexioColors.TextPrimary, fontSize = 13.sp)

/**
 * Kijelző FPS: a képernyő képkockaszámát méri a HUD saját képkocka-visszahívásaiból.
 * FIGYELEM: ez NEM a játék belső FPS-e (azt külső alkalmazás nem tudja megbízhatóan mérni),
 * hanem a kijelző frissítési üteme (pl. 60 / 90 / 120).
 */
object FpsModule : HudModule {
    override val id = "fps"
    override val name = "Kijelző FPS"
    override val description = "A kijelző frissítési üteme (nem a játék belső FPS-e)."
    override val category = ModuleCategory.INFO

    @Composable
    override fun Content() {
        var fps by remember { mutableStateOf(0) }
        DisposableEffect(Unit) {
            var frames = 0
            var last = System.nanoTime()
            val cb = object : Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    frames++
                    val now = System.nanoTime()
                    if (now - last >= 1_000_000_000L) { fps = frames; frames = 0; last = now }
                    Choreographer.getInstance().postFrameCallback(this)
                }
            }
            Choreographer.getInstance().postFrameCallback(cb)
            onDispose { Choreographer.getInstance().removeFrameCallback(cb) }
        }
        Text("FPS $fps", style = toolText)
    }
}

/**
 * Hálózati ping: TCP-kapcsolatfelépítési idő a 1.1.1.1:53 címre (Cloudflare DNS), 3 mp-enként.
 * Valódi mért érték, de az internetkapcsolatod késleltetését jelzi – NEM a játékszerverét.
 * Az INTERNET engedély kell a manifestben.
 */
object PingModule : HudModule {
    override val id = "ping"
    override val name = "Hálózati ping"
    override val description = "Mért késleltetés a 1.1.1.1 felé (a kapcsolatod minősége, nem a játékszerver)."
    override val category = ModuleCategory.INFO

    @Composable
    override fun Content() {
        var ms by remember { mutableStateOf<Int?>(null) }
        LaunchedEffect(Unit) {
            while (true) {
                ms = withContext(Dispatchers.IO) { measure() }
                delay(3000)
            }
        }
        Text(ms?.let { "PING $it ms" } ?: "PING –", style = toolText)
    }

    private fun measure(): Int? = try {
        Socket().use { s ->
            val t = System.nanoTime()
            s.connect(InetSocketAddress("1.1.1.1", 53), 1500)
            ((System.nanoTime() - t) / 1_000_000).toInt()
        }
    } catch (e: Exception) { null }
}

/** Jegyzetfüzet: a szöveget a Modulok képernyőn szerkesztheted, a HUD az első 3 sort mutatja. */
object NotesModule : HudModule {
    override val id = "notes"
    override val name = "Jegyzetfüzet"
    override val description = "Saját jegyzet a HUD-on (szerkesztés: Modulok képernyő)."
    override val category = ModuleCategory.TOOLS

    @Composable
    override fun Content() {
        val lines = ModuleSettings.notes.lines().filter { it.isNotBlank() }.take(3)
        if (lines.isEmpty()) Text("Nincs jegyzet", style = toolText.copy(color = NexioColors.TextSecondary))
        else lines.forEach { Text(it, style = toolText) }
    }
}
