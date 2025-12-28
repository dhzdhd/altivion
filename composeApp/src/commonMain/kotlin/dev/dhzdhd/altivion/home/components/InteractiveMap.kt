package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.home
import altivion.composeapp.generated.resources.plane
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import arrow.core.getOrElse
import arrow.core.toOption
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.services.Airplane
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.offset
import org.maplibre.compose.expressions.dsl.span
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

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
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
//                            val airplaneProps = features.first().properties
//                            val hex =
//                                airplaneProps?.getValue("hex").toOption().map { it.toString() }
//                            val airplane = airplaneValue.data.find { it.id == hex.getOrNull() }
//                            selectedAirplane = airplane

                            openBottomSheet = true
                            ClickResult.Consume
                        },
                        iconImage = image(markerPainter, drawAsSdf = true),
                        iconColor = const(Color.Blue),
                        iconSize = const(0.041f),
                        iconAllowOverlap = const(true),
                        iconIgnorePlacement = const(true),
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
        if (openBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                Column {
//                    Text(selectedAirplane?.airframe?.getOrNull() ?: "Unknown aircraft")
                }
            }
        }
    }
}
