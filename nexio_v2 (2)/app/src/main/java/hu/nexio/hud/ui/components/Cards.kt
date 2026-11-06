package hu.nexio.hud.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.nexio.hud.ui.theme.NexioColors

/** Áttetsző, finom keretes kártya. */
@Composable
fun GlassCard(highlight: Boolean = false, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexioColors.Surface),
        border = BorderStroke(1.dp, if (highlight) NexioColors.Purple else NexioColors.SurfaceStroke),
    ) { content() }
}

/** Címke + kapcsoló egy sorban. */
@Composable
fun SettingRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = NexioColors.TextSecondary, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
