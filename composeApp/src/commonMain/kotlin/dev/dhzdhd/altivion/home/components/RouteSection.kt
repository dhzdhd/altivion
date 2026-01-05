package dev.dhzdhd.altivion.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.RouteAndAirline

@Composable
fun RouteSection(routeAndAirline: Value<RouteAndAirline>) {
  when (routeAndAirline) {
    is Value.Data -> {
      AirportSection(routeAndAirline.data)
      TimeSection()
    }
    is Value.Loading -> CircularProgressIndicator()
    is Value.Error -> Text("Failed to load route information")
  }
}

@Composable
fun AirportSection(routeAndAirline: RouteAndAirline) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(
                text = routeAndAirline.route.origin.icaoCode,
                style =
                    MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
                color = Color.White)
            Text(
                    text = "(${routeAndAirline.route.origin.iataCode})",
            style =
                MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
            color = Color.White)
          Text(
              modifier = Modifier.padding(vertical = 10.dp),
              text = routeAndAirline.route.origin.name,
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFCCC2DC))
            Text(
                text = routeAndAirline.route.origin.municipality,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCC2DC))
          Text(
              text = routeAndAirline.route.origin.countryName,
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFCCC2DC))
        }
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  text = "✈",
                  fontSize = 24.sp,
              )

              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(2.dp)
                          .background(
                              brush =
                                  Brush.horizontalGradient(
                                      0f to Color(0xFF6750A4),
                                      0.5f to Color(0xFF6750A4),
                                      0.5f to Color(0x33FFFFFF),
                                      1f to Color(0x33FFFFFF))))
              Text(
                  text = "07h 34m",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFFCCC2DC),
                  fontSize = 11.sp)
            }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
          Text(
              text = routeAndAirline.route.destination.icaoCode,
              style =
                  MaterialTheme.typography.displaySmall.copy(
                      fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
              color = Color.White,
              textAlign = TextAlign.Right)
            Text(
                text = "(${routeAndAirline.route.destination.iataCode})",
                style =
                    MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
                color = Color.White,
                textAlign = TextAlign.Right)
            Text(
                modifier = Modifier.padding(vertical = 10.dp),
                text = routeAndAirline.route.destination.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCC2DC),textAlign = TextAlign.Right)
            Text(
                text = routeAndAirline.route.destination.municipality,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCC2DC),textAlign = TextAlign.Right)
            Text(
              text = routeAndAirline.route.destination.countryName,
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFCCC2DC),textAlign = TextAlign.Right)
        }
      }
}

@Composable
fun TimeSection() {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    TimeCard(
        modifier = Modifier.weight(1f), label = "DEPARTED", time = "1:06 PM", status = "Actual")
    TimeCard(
        modifier = Modifier.weight(1f),
        label = "ESTIMATED ARRIVAL",
        time = "1:12 AM",
        status = "Delayed")
  }
}

@Composable
private fun TimeCard(modifier: Modifier = Modifier, label: String, time: String, status: String) {
  Surface(
      modifier = modifier,
      shape = RoundedCornerShape(12.dp),
      color = Color(0x14FFFFFF),
      border = BorderStroke(1.dp, Color(0x1AFFFFFF))) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
              text = label,
              style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
              color = Color(0xFFCCC2DC),
              fontSize = 11.sp,
              modifier = Modifier.padding(bottom = 6.dp))

          Text(
              text = time,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
              color = Color.White,
              fontSize = 20.sp)

          Text(
              text = status,
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFA0A0A0),
              fontSize = 11.sp,
              modifier = Modifier.padding(top = 2.dp))
        }
      }
}
