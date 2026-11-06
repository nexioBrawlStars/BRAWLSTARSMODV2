package hu.nexio.hud.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import hu.nexio.hud.HudSettings
import hu.nexio.hud.OverlayController
import hu.nexio.hud.modules.ModuleCategory
import hu.nexio.hud.modules.ModuleRegistry
import hu.nexio.hud.modules.ModuleSettings
import hu.nexio.hud.ui.components.GlassCard
import hu.nexio.hud.ui.components.RgbText
import hu.nexio.hud.ui.components.SettingRow
import hu.nexio.hud.ui.theme.NexioColors
import hu.nexio.hud.ui.theme.ThemeManager
import hu.nexio.hud.ui.theme.ThemeProfile
import kotlin.math.roundToInt

private const val BRAWL_STARS_PACKAGE = "com.supercell.brawlstars"

/** Főképernyő: a régi 4 lépéses folyamat (engedély → HUD → megjelenés → játék) + modulok és téma. */
@Composable
fun MainScreen() {
    val ctx = LocalContext.current
    val activity = ctx as ComponentActivity

    // Az engedély állapotát minden visszatéréskor frissítjük (a rendszerbeállításokból jövünk vissza).
    var canDraw by remember { mutableStateOf(OverlayController.canDraw(ctx)) }
    DisposableEffect(activity) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) canDraw = OverlayController.canDraw(ctx)
        }
        activity.lifecycle.addObserver(obs)
        onDispose { activity.lifecycle.removeObserver(obs) }
    }

    var filter by remember { mutableStateOf<ModuleCategory?>(null) }
    val modules = ModuleRegistry.all().filter { filter == null || it.category == filter }

    LazyColumn(
        Modifier.fillMaxSize().background(NexioColors.Background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RgbText("NEXIO HUD", fontSize = 34.sp)
                Text("Lebegő HUD a hivatalos Brawl Stars mellé  •  V2", color = NexioColors.TextSecondary, fontSize = 12.sp)
            }
        }

        // 1. Engedély
        item {
            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Engedély", color = NexioColors.TextPrimary, fontSize = 16.sp)
                    Text(
                        if (canDraw) "Megjelenítés más alkalmazások fölött: engedélyezve ✓"
                        else "Megjelenítés más alkalmazások fölött: kikapcsolva",
                        color = NexioColors.TextSecondary, fontSize = 13.sp,
                    )
                    if (!canDraw) Button(onClick = { openOverlaySettings(ctx) }) { Text("Engedély megadása") }
                }
            }
        }

        // 2. HUD bekapcsolása
        item {
            GlassCard(highlight = HudSettings.enabled) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("2. HUD bekapcsolása", color = NexioColors.TextPrimary, fontSize = 16.sp)
                    Switch(
                        checked = HudSettings.enabled,
                        onCheckedChange = { on ->
                            if (on) {
                                if (!canDraw) {
                                    openOverlaySettings(ctx)
                                } else {
                                    HudSettings.setEnabled(ctx, true)
                                    OverlayController.start(ctx)
                                }
                            } else {
                                HudSettings.setEnabled(ctx, false)
                                OverlayController.stop(ctx)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = NexioColors.Purple),
                    )
                }
            }
        }

        // 3. Megjelenés
        item {
            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("3. Megjelenés", color = NexioColors.TextPrimary, fontSize = 16.sp)
                    Text("Átlátszóság: ${(HudSettings.opacity * 100).roundToInt()}%", color = NexioColors.TextSecondary, fontSize = 13.sp)
                    Slider(
                        value = HudSettings.opacity,
                        onValueChange = { HudSettings.setOpacity(ctx, it) },
                        valueRange = 0.3f..1f,
                    )
                    Text("Méret: ${(HudSettings.scale * 100).roundToInt()}%", color = NexioColors.TextSecondary, fontSize = 13.sp)
                    Slider(
                        value = HudSettings.scale,
                        onValueChange = { HudSettings.setScale(ctx, it) },
                        valueRange = 0.6f..1.6f,
                    )
                    Text(
                        "A HUD pozícióját a panel fejlécében lévő ⠿ fogóval húzva állíthatod. " +
                            "A ▾ gombbal összecsukhatod, hogy kevesebb helyet foglaljon.",
                        color = NexioColors.TextSecondary, fontSize = 12.sp,
                    )
                    OutlinedButton(onClick = {
                        HudSettings.resetPosition(ctx)
                        if (HudSettings.enabled) { OverlayController.stop(ctx); OverlayController.start(ctx) }
                    }) { Text("Pozíció visszaállítása") }
                }
            }
        }

        // 4. Játék indítása
        item {
            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("4. Játék indítása", color = NexioColors.TextPrimary, fontSize = 16.sp)
                    OutlinedButton(onClick = {
                        val i = ctx.packageManager.getLaunchIntentForPackage(BRAWL_STARS_PACKAGE)
                        if (i != null) ctx.startActivity(i)
                        else Toast.makeText(ctx, "A Brawl Stars nincs telepítve.", Toast.LENGTH_LONG).show()
                    }) { Text("Brawl Stars indítása") }
                }
            }
        }

        // 5. Modulok
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("5. Modulok", color = NexioColors.TextPrimary, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = filter == null, onClick = { filter = null }, label = { Text("Mind") })
                    ModuleCategory.values().forEach { c ->
                        FilterChip(selected = filter == c, onClick = { filter = c }, label = { Text(c.label) })
                    }
                }
            }
        }
        items(modules, key = { it.id }) { m ->
            val on = ModuleRegistry.enabled[m.id] == true
            GlassCard(highlight = on) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(m.name, color = NexioColors.TextPrimary, fontSize = 16.sp)
                        Text(m.description, color = NexioColors.TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = on,
                        onCheckedChange = { ModuleRegistry.setEnabled(ctx, m.id, it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = NexioColors.Purple),
                    )
                }
            }
        }

        // 6. Téma és modulbeállítások
        item {
            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("6. Téma és beállítások", color = NexioColors.TextPrimary, fontSize = 16.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeProfile.values().forEach { t ->
                            FilterChip(
                                selected = ThemeManager.profile == t,
                                onClick = { ThemeManager.set(ctx, t) },
                                label = { Text(t.label, fontSize = 11.sp) },
                            )
                        }
                    }
                    SettingRow("RGB betűanimáció", ModuleSettings.rgbAnimated) { ModuleSettings.setRgbAnimated(ctx, it) }
                    SettingRow("Brawl TV: SIM jelzés", ModuleSettings.showSimLabel) { ModuleSettings.setShowSimLabel(ctx, it) }
                    SettingRow("Brawl TV: be-/kilépések", ModuleSettings.showTvFeed) { ModuleSettings.setShowTvFeed(ctx, it) }
                }
            }
        }

        item {
            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Jegyzetfüzet", color = NexioColors.TextPrimary, fontSize = 15.sp)
                    OutlinedTextField(
                        value = ModuleSettings.notes,
                        onValueChange = { ModuleSettings.setNotes(ctx, it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Egy sor = egy jegyzet (a HUD az első 3-at mutatja)") },
                        minLines = 3,
                    )
                }
            }
        }
    }
}

private fun openOverlaySettings(ctx: android.content.Context) {
    ctx.startActivity(
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}
