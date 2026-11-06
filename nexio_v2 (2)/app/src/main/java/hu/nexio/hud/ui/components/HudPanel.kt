package hu.nexio.hud.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.nexio.hud.HudSettings
import hu.nexio.hud.StopwatchState
import hu.nexio.hud.modules.ModuleRegistry
import hu.nexio.hud.ui.theme.NexioColors
import kotlinx.coroutines.delay

/**
 * A lebegő HUD-panel: fejléc (NEXIO + összecsukás + húzófogó + bezárás),
 * időmérő Indít/Szünet és Nullázás gombokkal, alatta a bekapcsolt modulok.
 * A méretet és az átlátszóságot a HudSettings adja.
 */
@Composable
fun HudPanel(
    onDragBy: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onClose: () -> Unit,
) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { while (true) { StopwatchState.tick(); delay(50) } }

    val base = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(base.density * HudSettings.scale, base.fontScale)) {
        Surface(
            modifier = Modifier.alpha(HudSettings.opacity),
            shape = RoundedCornerShape(16.dp),
            color = NexioColors.Surface,
            border = BorderStroke(1.dp, NexioColors.SurfaceStroke),
        ) {
            Column(
                Modifier.padding(horizontal = 10.dp, vertical = 6.dp).widthIn(min = 150.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // --- Fejléc ---
                var dragStart by remember { mutableStateOf(Offset.Zero) }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RgbText("NEXIO", fontSize = 14.sp, letterSpacing = 3.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HeaderIcon(if (HudSettings.collapsed) "▴" else "▾") {
                            HudSettings.setCollapsed(ctx, !HudSettings.collapsed)
                        }
                        // Húzófogó: a panel pozícióját ezzel lehet mozgatni.
                        Box(
                            Modifier
                                .size(32.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { dragStart = it },
                                        onDragEnd = onDragEnd,
                                        onDragCancel = onDragEnd,
                                    ) { change, _ ->
                                        change.consume()
                                        // A panel mozog az ujj alatt, ezért a kezdőponthoz mért eltolást használjuk.
                                        onDragBy(change.position.x - dragStart.x, change.position.y - dragStart.y)
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) { Text("⠿", color = NexioColors.TextSecondary, fontSize = 18.sp) }
                        HeaderIcon("✕", onClick = onClose)
                    }
                }

                // --- Időmérő ---
                Text(
                    StopwatchState.format(),
                    color = NexioColors.TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                )

                if (!HudSettings.collapsed) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val startLabel = when {
                            StopwatchState.running -> "Szünet"
                            StopwatchState.elapsedMs > 0 -> "Folytat"
                            else -> "Indít"
                        }
                        PanelButton(startLabel, Modifier.weight(1f)) { StopwatchState.toggle() }
                        PanelButton("Nullázás", Modifier.weight(1f)) { StopwatchState.reset() }
                    }
                }

                // --- Bekapcsolt modulok ---
                ModuleRegistry.active().forEach { m -> key(m.id) { m.Content() } }
            }
        }
    }
}

@Composable
private fun HeaderIcon(glyph: String, onClick: () -> Unit) {
    Box(Modifier.size(32.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(glyph, color = NexioColors.TextSecondary, fontSize = 16.sp)
    }
}

@Composable
private fun PanelButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        border = BorderStroke(1.dp, NexioColors.Purple.copy(alpha = 0.6f)),
    ) {
        // Egy sorban marad: a régi verzió "Nul láz" tördelési hibája itt nem fordulhat elő.
        Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false, color = NexioColors.TextPrimary)
    }
}
