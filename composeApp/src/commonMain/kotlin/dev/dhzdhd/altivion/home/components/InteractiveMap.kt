package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.plane
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.toOption
import coil3.compose.AsyncImage
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.repositories.AirplaneImage
import dev.dhzdhd.altivion.home.services.HomeService
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
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
    restore = { Json.decodeFromString<Airplane?>(it) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMap(airplaneValue: Value<List<Airplane>>) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()
    val baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")

    val openBottomSheetState = rememberSaveable { mutableStateOf(false) }
    var selectedAirplane by rememberSaveable(stateSaver = AirplaneStateSaver) { mutableStateOf(null) }

    val markerPainter = painterResource(Res.drawable.plane)

    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = baseStyle,
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            if (airplaneValue is Value.Data) {
                val features = airplaneValue.data.map { airplane ->
                    val point =
                        Point.fromGeoUri("geo:${airplane.latitude},${airplane.longitude}")
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
                        val hexOption =
                            airplaneProps?.getValue("hex").toOption()
                                .map { it.toString().trimStart('"').trimEnd('"') }
                        val hex =
                            hexOption.getOrNull()
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
            println(req)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { openBottomSheetState.value = false },
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp).scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (airplane != null) {
                HeaderSection(airplane)
                ImageSection(airplane, airplaneImage)
                RouteSection(airplane)
            } else {
                Text("Error in retrieving aircraft details")
            }
        }
    }
}

@Composable
fun RouteSection(airplane: Airplane) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderSection(airplane: Airplane) {
    LazyVerticalGrid(columns = GridCells.Fixed(count = 2)) {
        item {
            Text(
                airplane.flight.getOrElse { "?" },
                fontSize = 9.em,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "In progress",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
        item(span = { GridItemSpan(2) }) {
            Column {
                Text(airplane.description.getOrElse { "Unknown aircraft" })
                Text(airplane.registration.getOrElse { "Unknown registration" })
            }
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
                airplaneImage.error.message,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
