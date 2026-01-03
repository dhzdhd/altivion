package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.arrow_right
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arrow.core.getOrElse
import dev.dhzdhd.altivion.home.models.Airplane
import org.jetbrains.compose.resources.painterResource

data class FlightInfoCardDetails(val title: String, val subtitle: String?, val value: String)

@Composable
fun FlightInfoSection(airplane: Airplane) {
  FlightInfoCard(
      "Aircraft Details",
      listOf(
          FlightInfoCardDetails("Type", null, airplane.type.getOrElse { "N/A" }),
          FlightInfoCardDetails("Airframe", null, airplane.airframe.getOrElse { "N/A" }),
          FlightInfoCardDetails("Category", null, airplane.category.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "ADS-B Version",
              null,
              airplane.adsbVersion.map { it.toString() }.getOrElse { "N/A" }),
      ))
  FlightInfoCard(
      "Flight Performance",
      listOf(
          FlightInfoCardDetails(
              "True Airspeed",
              "kts",
              airplane.trueAirSpeed.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Ground Speed",
              "kts",
              airplane.groundSpeed.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Mach", null, airplane.mach.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Wind Direction",
              "°",
              airplane.windDirection.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Wind Speed", "kts", airplane.windSpeed.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Outside Air Temp",
              "°C",
              airplane.outsideAirTemperature.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Total Air Temp",
              "°C",
              airplane.totalAirTemperature.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Geometric Alt",
              "ft",
              airplane.geometricAltitude.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Magnetic Heading",
              "°",
              airplane.magneticHeading.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "True Heading", "°", airplane.trueHeading.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails("Squawk", null, airplane.squawk.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Time Since Last Msg",
              "s",
              airplane.timeSinceLastMessage.map { it.toString() }.getOrElse { "N/A" }),
          FlightInfoCardDetails(
              "Signal Strength",
              "dBm",
              airplane.signalStrength.map { it.toString() }.getOrElse { "N/A" }),
      ))
}

@Composable
fun FlightInfoCard(title: String, details: List<FlightInfoCardDetails>) {
  var isExpanded by remember { mutableStateOf(true) }

  Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF2B2930),
      shadowElevation = 2.dp,
      border = BorderStroke(1.dp, Color(0x0DFFFFFF))) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .clickable { isExpanded = !isExpanded }
                      .padding(bottom = if (isExpanded) 8.dp else 0.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFE6E1E5),
                    fontSize = 16.sp)

                Icon(
                    painter = painterResource(Res.drawable.arrow_right),
                    contentDescription = "",
                    modifier = Modifier.size(20.dp).rotate(if (isExpanded) 90f else 0f))
              }

          if (isExpanded) {
            details.mapIndexed { index, detail ->
              FlightInfoItemRow(
                  title = detail.title,
                  subtitle = detail.subtitle,
                  value = detail.value,
                  showDivider = index != details.lastIndex)
            }
          }
        }
      }
}

@Composable
private fun FlightInfoItemRow(
    title: String,
    subtitle: String?,
    value: String,
    showDivider: Boolean = true
) {
  Column {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFFE6E1E5),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 2.dp))

            if (subtitle != null) {
              Text(
                  text = subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFFCAC4D0),
                  fontSize = 12.sp)
            }
          }
          Text(
              text = value,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = Color(0xFFE6E1E5),
              fontSize = 16.sp)
        }
    if (showDivider) {
      HorizontalDivider(color = Color(0x14FFFFFF), thickness = 1.dp)
    }
  }
}
