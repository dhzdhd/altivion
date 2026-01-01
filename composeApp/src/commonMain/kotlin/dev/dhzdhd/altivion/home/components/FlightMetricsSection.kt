package dev.dhzdhd.altivion.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arrow.core.getOrElse
import dev.dhzdhd.altivion.home.models.Airplane

@Composable
fun FlightMetricsSection(airplane: Airplane) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = "🏔️",
            value = airplane.barometricAltitude.getOrElse { "?" },
            label = "Altitude (ft)"
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = "⚡",
            value = airplane.indicatedAirSpeed.map { it.toString() }.getOrElse { "?" },
            label = "Speed (kts)"
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = "🧭",
            value = airplane.track.map { it.toString() }.getOrElse { "?" },
            label = "Track"
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier, icon: String, value: String, label: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2B2930),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon, fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFE6E1E5),
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = label, style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.3.sp
                ), color = Color(0xFFCAC4D0), fontSize = 11.sp, textAlign = TextAlign.Center
            )
        }
    }
}
