package dev.dhzdhd.altivion.home.components

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.home
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.services.Airplane
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
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
import org.maplibre.compose.sources.GeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point

@Composable
fun InteractiveMap(airplaneValue: Value<List<Airplane>>) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()
    val baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")

    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = baseStyle,
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            when (airplaneValue) {
                is Value.Data -> {
                    println(airplaneValue.data)
                    val marker = painterResource(Res.drawable.home)
                    val features = airplaneValue.data.map { airplane ->
                        val point =
                            Point.fromGeoUri("geo:${airplane.latitude},${airplane.longitude}")
                        Feature(geometry = point, properties = airplane)
                    }
                    val featureCollection = FeatureCollection(features)
                    val geoJsonSource = GeoJsonSource(
                        "airplanes",
                        GeoJsonData.Features(featureCollection),
                        GeoJsonOptions()
                    )

                    SymbolLayer(
                        id = "airplanes",
                        source = geoJsonSource,
                        onClick = { features ->
                            ClickResult.Consume
                        },
                        iconImage = image(marker),
                        textField =
                            format(
                                span(image("railway")),
                            ),
                        textFont = const(listOf("Noto Sans Regular")),
                        textColor = const(MaterialTheme.colorScheme.onBackground),
                        textOffset = offset(0.em, 0.6.em),
                    )
                }

                else -> {
                    // Do nothing for now
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
    }
}