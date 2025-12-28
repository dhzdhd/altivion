package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.plane
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import arrow.core.toOption
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.Airplane
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.plus
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMap(airplaneValue: Value<List<Airplane>>) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()
    val baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")

    var openBottomSheetState = rememberSaveable { mutableStateOf(false) }
    var selectedAirplane by rememberSaveable { mutableStateOf<Airplane?>(null) }

    val markerPainter = painterResource(Res.drawable.plane)

    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = baseStyle,
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            when (airplaneValue) {
                is Value.Data -> {
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
                            val hexOption =
                                airplaneProps?.getValue("hex").toOption().map { it.toString() }
                            val hex = hexOption.map { it.trimEnd('"').trimStart('"') }.getOrNull()
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
                        iconRotate = get("track").asNumber().plus(const(270.0f))
                    )
                }

                else -> {
                }
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
        if (openBottomSheetState.value) {
            AirplaneInfoBottomSheet(selectedAirplane, openBottomSheetState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirplaneInfoBottomSheet(airplane: Airplane?, openBottomSheetState: MutableState<Boolean>) {
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = { openBottomSheetState.value = false },
        sheetState = bottomSheetState
    ) {
        Column {
            Text(airplane?.airframe?.getOrNull() ?: "Unknown aircraft")
        }
    }
}


@Preview
@Composable
fun AirplaneInfoBottomSheetPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text("Airframe")
        }
    }
}


