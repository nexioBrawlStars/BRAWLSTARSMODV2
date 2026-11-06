package hu.nexio.hud.modules.builtin

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.nexio.hud.modules.HudModule
import hu.nexio.hud.modules.ModuleCategory
import hu.nexio.hud.modules.ModuleSettings
import hu.nexio.hud.ui.theme.NexioColors
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Brawl TV (vizuális elem) – SZIMULÁLT nézőszám, csak a saját képernyődön.
 * Nem olvas játékadatot, nem kommunikál a játékkal; a nézők botok.
 * A nézőszám 0 és MAX_VIEWERS között mozog, folyamatos be-/kilépésekkel.
 */
object BrawlTvModule : HudModule {
    const val MAX_VIEWERS = 800

    override val id = "brawl_tv"
    override val name = "Brawl TV (vizuális)"
    override val description = "Szimulált élő nézőszám be-/kilépő botokkal, max. 800 néző. Csak nálad látszik, a játék fölötti overlayként."
    override val category = ModuleCategory.DISPLAY

    private val names = listOf(
        "NovaFox", "PixelWolf", "StarDust", "VoltRider", "NeonKid", "ShadowByte", "LunaStrike",
        "GigaBull", "ZeroPing", "FrostByte", "CrimsonAce", "MintyBear", "TurboToast", "OrbitX",
        "Kékmókus", "Villám77", "HoldFény", "NagyPanda", "Rókalány", "Csillag_88",
    )

    @Composable
    override fun Content() {
        var viewers by remember { mutableStateOf(Random.nextInt(8, 40)) }
        val feed = remember { mutableStateListOf<String>() }

        LaunchedEffect(Unit) {
            var trend = 1 // +1 növekvő, -1 csökkenő szakasz
            var ticks = 0
            while (true) {
                delay(Random.nextLong(700, 2200))
                ticks++
                if (ticks % Random.nextInt(8, 16) == 0) trend = -trend
                // Kis lépések, enyhe irányú trenddel – természetesnek hat.
                val join = Random.nextInt(0, if (trend > 0) 14 else 7)
                val leave = Random.nextInt(0, if (trend > 0) 6 else 12)
                val next = (viewers + join - leave).coerceIn(0, MAX_VIEWERS)
                if (join > leave && next != viewers) feed.add("${names.random()} csatlakozott")
                else if (leave > join && next != viewers) feed.add("${names.random()} kilépett")
                viewers = next
                while (feed.size > 3) feed.removeAt(0)
            }
        }

        val pulse by rememberInfiniteTransition(label = "live").animateFloat(
            initialValue = 0.35f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "liveDot",
        )

        Column(verticalArrangement = Arrangement.spacedBy(3.dp), horizontalAlignment = Alignment.End) {
            // Kompakt, játékszerű "pirula": sötét háttér, piros LIVE pont, szem + nézőszám.
            Row(
                Modifier
                    .background(Color(0xCC0B0A14), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(Modifier.alpha(pulse).size(8.dp).background(Color(0xFFFF3B5C), CircleShape)) {}
                Text("\uD83D\uDC41", fontSize = 12.sp)
                Text("$viewers", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (ModuleSettings.showSimLabel) {
                    Text("SIM", color = NexioColors.TextSecondary, fontSize = 8.sp)
                }
            }
            if (ModuleSettings.showTvFeed) {
                feed.forEach { Text(it, color = Color(0xCCFFFFFF), fontSize = 10.sp) }
            }
        }
    }
}
