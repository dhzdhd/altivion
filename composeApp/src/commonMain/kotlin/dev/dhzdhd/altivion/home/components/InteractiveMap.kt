package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.plane
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.toOption
import coil3.compose.AsyncImage
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.repositories.AirplaneImage
import dev.dhzdhd.altivion.home.services.HomeService
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
import org.maplibre.compose.expressions.dsl.all
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.plus
import org.maplibre.compose.expressions.value.IconRotationAlignment
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.DisappearingCompassButton
import org.maplibre.compose.material3.DisappearingScaleBar
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point

private val AirplaneStateSaver = Saver<Airplane?, String>(
    save = { Json.encodeToString(it) },
    restore = { Json.decodeFromString<Airplane?>(it) })

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun InteractiveMap(airplaneValue: Value<List<Airplane>>) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()
    val baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")

    val openBottomSheetState = rememberSaveable { mutableStateOf(false) }
    var selectedAirplane by rememberSaveable(stateSaver = AirplaneStateSaver) { mutableStateOf(null) }

    val markerPainter = painterResource(Res.drawable.plane)

    val permissionState = rememberPermissionState(Permission.FineLocation)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = baseStyle,
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            if (airplaneValue is Value.Data) {
                val features = airplaneValue.data.map { airplane ->
                    val point = Point.fromGeoUri("geo:${airplane.latitude},${airplane.longitude}")
                    Feature(geometry = point, properties = airplane)
                }
                val featureCollection = FeatureCollection(features)
                val source = rememberGeoJsonSource(
                    data = GeoJsonData.Features(featureCollection),
                    options = GeoJsonOptions(minZoom = 0)
                )

                SymbolLayer(
                    id = "airplanes",
                    source = source,
                    onClick = { features ->
                        val airplaneProps = features.first().properties
//                        selectedAirplane = Json.decodeFromString<Airplane?>(airplaneProps.toString())
                        val hexOption = airplaneProps?.getValue("hex").toOption()
                            .map { it.toString().trimStart('"').trimEnd('"') }
                        val hex = hexOption.getOrNull()
                        val airplane = airplaneValue.data.find { it.hex.contentEquals(hex) }

                        selectedAirplane = airplane

                        openBottomSheetState.value = true
                        ClickResult.Consume
                    },
                    iconImage = image(markerPainter, drawAsSdf = true),
                    iconColor = const(Color.Blue),
                    iconSize = const(0.041f),
                    iconAllowOverlap = const(true),
                    iconIgnorePlacement = const(true),
                    iconRotate = get("track").asNumber().plus(const(270.0f)),
                    iconRotationAlignment = const(IconRotationAlignment.Map)
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            DisappearingScaleBar(
                metersPerDp = cameraState.metersPerDpAtTarget,
                zoom = cameraState.position.zoom,
                modifier = Modifier.align(Alignment.TopStart),
            )
            DisappearingCompassButton(
                cameraState, modifier = Modifier.align(Alignment.TopEnd)
            )
            ExpandingAttributionButton(
                cameraState = cameraState,
                styleState = styleState,
                modifier = Modifier.align(Alignment.BottomEnd),
                contentAlignment = Alignment.BottomEnd,
            )
            FloatingActionButton(onClick = {
                coroutineScope.launch {
//                    if (permissionState.status.isNotGranted) {
//                        permissionState.launchPermissionRequest()
//                    }
//
//                    if (permissionState.status.isGranted) {
//                        println(locationProvider.location)
//                        val location = locationProvider.location.value
//                        println(location)
//                        cameraState.animateTo(
//                            CameraPosition(
//                                target = Position(
//                                    longitude = location?.position?.longitude!!,
//                                    latitude = location.position.latitude
//                                )
//                            )
//                        )
//                    }
                }
            }) {
                Text("Location")
            }
        }
        if (airplaneValue is Value.Loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }

    if (openBottomSheetState.value) {
        AirplaneInfoBottomSheet(selectedAirplane, openBottomSheetState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirplaneInfoBottomSheet(
    airplane: Airplane?,
    openBottomSheetState: MutableState<Boolean>,
    homeService: HomeService = koinInject()
) {
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    var airplaneImage by remember { mutableStateOf<Value<AirplaneImage>>(Value.Loading) }

    if (airplane != null) {
        LaunchedEffect(airplane) {
            val req = homeService.getAirplaneImage(airplane)
            airplaneImage = when (req) {
                is Either.Right -> Value.Data(req.value)
                is Either.Left -> Value.Error(req.value)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { openBottomSheetState.value = false },
        sheetState = bottomSheetState,
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp).verticalScroll(
                state = rememberScrollState(),
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (airplane != null) {
                HeaderSection(airplane)
                ImageSection(airplane, airplaneImage)
                RouteSection()
                TimeSection()
                FlightMetricsSection(airplane)
                FlightInfoSection(airplane)
            } else {
                Text("Error in retrieving aircraft details")
            }
        }
    }
}

@Composable
fun FlightInfoSection(airplane: Airplane) {
    FlightInfoCard(
        "Aircraft Details", listOf(
            FlightInfoCardDetails("Type", null, airplane.type.getOrElse { "N/A" }),
            FlightInfoCardDetails("Airframe", null, airplane.airframe.getOrElse { "N/A" }),
            FlightInfoCardDetails("Category", null, airplane.category.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "ADS-B Version",
                null,
                airplane.adsbVersion.map { it.toString() }.getOrElse { "N/A" }),
        )
    )
    FlightInfoCard(
        "Flight Performance", listOf(
            FlightInfoCardDetails(
                "True Airspeed",
                "kts",
                airplane.trueAirSpeed.map { it.toString() }.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "Ground Speed",
                "kts",
                airplane.groundSpeed.map { it.toString() }.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "Mach",
                null,
                airplane.mach.map { it.toString() }.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "Wind Direction",
                "°",
                airplane.windDirection.map { it.toString() }.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "Wind Speed",
                "kts",
                airplane.windSpeed.map { it.toString() }.getOrElse { "N/A" }),
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
                "True Heading",
                "°",
                airplane.trueHeading.map { it.toString() }.getOrElse { "N/A" }),
            FlightInfoCardDetails("Squawk", null, airplane.squawk.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "Time Since Last Msg",
                "s",
                airplane.timeSinceLastMessage.map { it.toString() }.getOrElse { "N/A" }),
            FlightInfoCardDetails(
                "Signal Strength",
                "dBm",
                airplane.signalStrength.map { it.toString() }.getOrElse { "N/A" }),
        )
    )
}

data class FlightInfoCardDetails(val title: String, val subtitle: String?, val value: String)

@Composable
fun FlightInfoCard(title: String, details: List<FlightInfoCardDetails>) {
    var isExpanded by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF2B2930),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }
                .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title, style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ), color = Color(0xFFE6E1E5), fontSize = 16.sp
                )

                Text(
                    text = if (isExpanded) "⌄" else "›", color = Color(0xFFCAC4D0), fontSize = 20.sp
                )
            }

            details.mapIndexed { index, detail ->
                FlightInfoItemRow(
                    title = detail.title,
                    subtitle = detail.subtitle,
                    value = detail.value,
                    showDivider = index != details.lastIndex
                )
            }
        }
    }
}

@Composable
private fun FlightInfoItemRow(
    title: String, subtitle: String?, value: String, showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFFE6E1E5),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCAC4D0),
                        fontSize = 12.sp
                    )
                }
            }
            Text(
                text = value, style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ), color = Color(0xFFE6E1E5), fontSize = 16.sp
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = Color(0x14FFFFFF), thickness = 1.dp
            )
        }
    }
}

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

@Composable
fun TimeSection() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TimeCard(
            modifier = Modifier.weight(1f), label = "DEPARTED", time = "1:06 PM", status = "Actual"
        )
        TimeCard(
            modifier = Modifier.weight(1f),
            label = "ESTIMATED ARRIVAL",
            time = "1:12 AM",
            status = "Delayed"
        )
    }
}

@Composable
private fun TimeCard(
    modifier: Modifier = Modifier, label: String, time: String, status: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0x14FFFFFF),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFFCCC2DC),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = time, style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ), color = Color.White, fontSize = 20.sp
            )

            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFA0A0A0),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}


@Composable
fun RouteSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "AMS", style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp
                ), color = Color.White
            )
            Text(
                text = "Amsterdam",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCC2DC)
            )
        }
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "✈",
                fontSize = 24.sp,
            )

            Box(
                modifier = Modifier.fillMaxWidth().height(2.dp).background(
                    brush = Brush.horizontalGradient(
                        0f to Color(0xFF6750A4),
                        50f to Color(0xFF6750A4),
                        50f to Color(0x33FFFFFF),
                        1f to Color(0x33FFFFFF)
                    )
                )
            )
            Text(
                text = "07h 34m",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCCC2DC),
                fontSize = 11.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "DEL", style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp
                ), color = Color.White
            )
            Text(
                text = "Delhi",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCC2DC)
            )
        }
    }
}

@Composable
private fun HeaderSection(airplane: Airplane) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                airplane.flight.getOrElse { "?" },
                fontSize = 9.em,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "In progress",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
        Column {
            Text(airplane.description.getOrElse { "Unknown aircraft" })
            Text(airplane.registration.getOrElse { "Unknown registration" })
        }
    }
}

@Composable
private fun ImageSection(airplane: Airplane, airplaneImage: Value<AirplaneImage>) {
    Box(
        modifier = Modifier.fillMaxWidth().height(250.dp).border(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary),
            shape = RoundedCornerShape(20.dp)
        )
    ) {
        when (airplaneImage) {
            is Value.Data -> {
                AsyncImage(
                    airplaneImage.data.image,
                    contentDescription = airplaneImage.data.link,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.FillBounds,
                )
            }

            is Value.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is Value.Error -> Text(
                airplaneImage.error.message, modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
