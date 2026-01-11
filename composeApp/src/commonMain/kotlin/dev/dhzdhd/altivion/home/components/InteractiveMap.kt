package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.location
import altivion.composeapp.generated.resources.plane
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatten
import arrow.core.toOption
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.models.Airport
import dev.dhzdhd.altivion.home.models.Location
import dev.dhzdhd.altivion.home.models.RouteAndAirline
import dev.dhzdhd.altivion.home.repositories.AirplaneImage
import dev.dhzdhd.altivion.home.services.HomeService
import dev.dhzdhd.altivion.home.viewmodels.HomeAction
import dev.dhzdhd.altivion.home.viewmodels.HomeViewModel
import dev.dhzdhd.altivion.settings.viewmodels.MapStyle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.condition
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.eq
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.plus
import org.maplibre.compose.expressions.dsl.switch
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
import org.maplibre.spatialk.geojson.Position

private val AirplaneStateSaver =
    Saver<Airplane?, String>(
        save = { Json.encodeToString(it) }, restore = { Json.decodeFromString<Airplane?>(it) })

private val AirportStateSaver =
    Saver<Airport?, String>(
        save = { Json.encodeToString(it) }, restore = { Json.decodeFromString<Airport?>(it) })


private fun getAltitude(alt: Option<String>): String {
    return when (alt) {
        is None -> "0"
        is Some if alt.value.contentEquals("ground") -> "0"
        is Some -> alt.value
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, FlowPreview::class)
@Composable
fun InteractiveMap(
    viewModel: HomeViewModel,
    mapStyle: MapStyle,
    service: HomeService = koinInject()
) {
    val airplaneValue by viewModel.airplanes.collectAsState()

    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()
    val baseStyle = BaseStyle.Uri(mapStyle.url)

    val openAirplaneBottomSheetState = rememberSaveable { mutableStateOf(false) }
    val openAirportBottomSheetState = rememberSaveable { mutableStateOf(false) }
    var selectedAirplane by rememberSaveable(stateSaver = AirplaneStateSaver) { mutableStateOf(null) }
    var selectedAirport by rememberSaveable(stateSaver = AirportStateSaver) { mutableStateOf(null) }
    var selectedAirlineAndRoute: Value<RouteAndAirline> by remember { mutableStateOf(Value.Loading) }
    var currentLocation: Option<Location> by remember { mutableStateOf(None) }

    val airplanePainter = painterResource(Res.drawable.plane)
    val locationPainter = painterResource(Res.drawable.location)

    val permissionState = rememberPermissionState(Permission.FineLocation)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(
        selectedAirplane?.hex,
        {
            selectedAirlineAndRoute =
                Value.fromEither(
                    selectedAirplane
                        ?.let { service.getAirplaneRouteAndAirline(it.flight.map { s -> s.trim() }) }
                        .toOption()
                        .toEither {
                            AppError.UnknownError("Route for selected airplane is not available")
                        }
                        .flatten())
        })

    LaunchedEffect(Unit) {
        snapshotFlow { cameraState.position }
            .debounce(3000)
            .collect { position ->
                viewModel.dispatch(HomeAction.UpdateCameraState(position.target, position.zoom))
            }
    }

    LaunchedEffect(Unit) {
        val location = service.getLocation().getOrNull()

        if (location != null) {
            cameraState.animateTo(
                CameraPosition(
                    target = Position(longitude = location.longitude, latitude = location.latitude),
                    zoom = 5.0
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = baseStyle,
            cameraState = cameraState,
            styleState = styleState,
            zoomRange = 2f..15f,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            if (selectedAirlineAndRoute is Value.Data) {
                val route = (selectedAirlineAndRoute as Value.Data<RouteAndAirline>).data.route
                val originPoint =
                    Point.fromGeoUri("geo:${route.origin.latitude},${route.origin.longitude}")
                val origin = Feature(geometry = originPoint, properties = route.origin)

                val destinationPoint =
                    Point.fromGeoUri("geo:${route.destination.latitude},${route.destination.longitude}")
                val destination =
                    Feature(geometry = destinationPoint, properties = route.destination)

                val featureCollection = FeatureCollection(listOf(origin, destination))
                val source =
                    rememberGeoJsonSource(
                        data = GeoJsonData.Features(featureCollection),
                    )

                SymbolLayer(
                    id = "selected_airport",
                    source = source,
                    iconImage = image(locationPainter, drawAsSdf = true),
                    iconColor = const(MaterialTheme.colorScheme.secondary),
                    iconSize = const(1.5f),
                    iconAllowOverlap = const(true),
                    iconIgnorePlacement = const(true),
                    onClick = { features ->
                        val airportProps = features.first().properties
                        val icao = airportProps?.getValue("icaoCode").toString().trimStart('"').trimEnd('"')
                        println(icao)
                        val airport = if (route.origin.icaoCode.contentEquals(icao)) {
                            route.origin
                        } else {
                            route.destination
                        }

                        selectedAirport = airport
                        openAirportBottomSheetState.value = true
                        ClickResult.Consume
                    }
                )
            }
            if (airplaneValue is Value.Data) {
                val features =
                    (airplaneValue as Value.Data<List<Airplane>>).data.map { airplane ->
                        val point =
                            Point.fromGeoUri(
                                "geo:${airplane.latitude},${airplane.longitude},${
                                    getAltitude(
                                        airplane.barometricAltitude
                                    )
                                }"
                            )
                        Feature(geometry = point, properties = airplane)
                    }
                val featureCollection = FeatureCollection(features)
                val source =
                    rememberGeoJsonSource(
                        data = GeoJsonData.Features(featureCollection),
                        options = GeoJsonOptions(minZoom = 0)
                    )

                SymbolLayer(
                    id = "airplanes",
                    source = source,
                    onClick = { features ->
                        selectedAirlineAndRoute = Value.Loading

                        val airplaneProps = features.first().properties
                        val hexOption =
                            airplaneProps?.getValue("hex").toOption().map {
                                it.toString().trimStart('"').trimEnd('"')
                            }
                        val hex = hexOption.getOrNull()
                        val airplane =
                            (airplaneValue as Value.Data<List<Airplane>>).data.find {
                                it.hex.contentEquals(hex)
                            }

                        selectedAirplane = airplane

                        openAirplaneBottomSheetState.value = true
                        ClickResult.Consume
                    },
                    iconImage = image(airplanePainter, drawAsSdf = true),
                    iconColor =
                        switch(
                            condition(
                                feature["hex"].asString().eq(const(selectedAirplane?.hex ?: "")),
                                const(MaterialTheme.colorScheme.tertiaryFixed)
                            ),
                            fallback = const(MaterialTheme.colorScheme.primary)
                        ),
                    iconSize = const(0.041f),
                    iconAllowOverlap = const(true),
                    iconIgnorePlacement = const(true),
                    iconRotate = get("track").asNumber().plus(const(270.0f)),
                    iconRotationAlignment = const(IconRotationAlignment.Map),
                    //                    textField = feature["flight"].asString()
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            DisappearingScaleBar(
                metersPerDp = cameraState.metersPerDpAtTarget,
                zoom = cameraState.position.zoom,
                modifier = Modifier.align(Alignment.TopStart),
            )
            DisappearingCompassButton(cameraState, modifier = Modifier.align(Alignment.TopEnd))
            ExpandingAttributionButton(
                cameraState = cameraState,
                styleState = styleState,
                modifier = Modifier.align(Alignment.TopEnd),
                contentAlignment = Alignment.BottomEnd,
            )
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

    if (openAirplaneBottomSheetState.value) {
        AirplaneInfoBottomSheet(selectedAirplane, openAirplaneBottomSheetState, selectedAirlineAndRoute)
    }
    if (openAirportBottomSheetState.value) {
        AirportInfoBottomSheet(selectedAirport, openAirportBottomSheetState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportInfoBottomSheet(
    airport: Airport?,
    openBottomSheetState: MutableState<Boolean>,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { openBottomSheetState.value = false },
        sheetState = sheetState,
        containerColor = Color(0xFF1C1B1F),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with Airport Codes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = airport?.icaoCode ?: "?",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-2).sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = airport?.iataCode ?: "?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = airport?.name ?: "?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    title = "Location",
                    items = listOf(
                        InfoItem("City", airport?.municipality ?: "?"),
                        InfoItem("Country", airport?.countryName ?: "?"),
                        InfoItem("Country Code", airport?.countryISOName ?: "?")
                    )
                )

                InfoCard(
                    title = "Coordinates & Elevation",
                    items = listOf(
                        InfoItem("Latitude", airport?.latitude.toString()),
                        InfoItem("Longitude", airport?.longitude.toString()),
                        InfoItem("Elevation", "${airport?.elevation?.toInt()} ft")
                    )
                )
            }
        }
    }
}

data class InfoItem(val label: String, val value: String)

@Composable
fun InfoCard(
    title: String,
    items: List<InfoItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 5.em
                ),
                color = MaterialTheme.colorScheme.primary
            )
            items.forEach { item ->
                InfoRow(label = item.label, value = item.value)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFB3B3B3)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.White,
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirplaneInfoBottomSheet(
    airplane: Airplane?,
    openBottomSheetState: MutableState<Boolean>,
    routeAndAirline: Value<RouteAndAirline>,
    homeService: HomeService = koinInject()
) {
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded,
        )
    var airplaneImage by remember { mutableStateOf<Value<AirplaneImage>>(Value.Loading) }

    if (airplane != null) {
        LaunchedEffect(airplane) {
            val req = homeService.getAirplaneImage(airplane)
            airplaneImage =
                when (req) {
                    is Either.Right -> Value.Data(req.value)
                    is Either.Left -> Value.Error(req.value)
                }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { openBottomSheetState.value = false },
        sheetState = bottomSheetState,
//        properties = ModalBottomSheetProperties(shouldDismissOnClickOutside = false),
        scrimColor = Color.Transparent
    ) {
        Column(
            modifier =
                Modifier.padding(all = 16.dp)
                    .verticalScroll(
                        state = rememberScrollState(),
                    ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (airplane != null) {
                HeaderSection(airplane)
                ImageSection(airplaneImage)
                RouteSection(routeAndAirline)
                FlightMetricsSection(airplane)
                FlightInfoSection(airplane)
            } else {
                Text("Error in retrieving aircraft details")
            }
        }
    }
}
