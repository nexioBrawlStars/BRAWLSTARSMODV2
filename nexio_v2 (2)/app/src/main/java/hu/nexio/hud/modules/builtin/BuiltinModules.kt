package hu.nexio.hud.modules.builtin

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import hu.nexio.hud.modules.HudModule
import hu.nexio.hud.modules.ModuleCategory
import hu.nexio.hud.ui.theme.NexioColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val hudText = TextStyle(color = NexioColors.TextPrimary, fontSize = 13.sp)

object ClockModule : HudModule {
    override val id = "clock"
    override val name = "Óra"
    override val description = "Pontos helyi idő a HUD-on."
    override val category = ModuleCategory.INFO
    override val defaultEnabled = true

    @Composable
    override fun Content() {
        var now by remember { mutableStateOf("--:--:--") }
        LaunchedEffect(Unit) {
            val fmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            while (true) { now = fmt.format(Date()); delay(1000) }
        }
        Text(now, style = hudText)
    }
}

object BatteryModule : HudModule {
    override val id = "battery"
    override val name = "Akkumulátor"
    override val description = "Töltöttség és hőmérséklet (a telefon saját adatai)."
    override val category = ModuleCategory.INFO

    @Composable
    override fun Content() {
        val ctx = LocalContext.current
        var text by remember { mutableStateOf("…") }
        LaunchedEffect(Unit) {
            while (true) { text = read(ctx); delay(5000) }
        }
        Text(text, style = hudText)
    }

    private fun read(ctx: Context): String {
        val i: Intent? = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = i?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = i?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val temp = (i?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        val pct = if (level >= 0) level * 100 / scale else 0
        return "BAT $pct%  ${"%.1f".format(temp)}°C"
    }
}
