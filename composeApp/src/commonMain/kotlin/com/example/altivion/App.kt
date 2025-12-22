package com.example.altivion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.compose_multiplatform
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.DisappearingCompassButton
import org.maplibre.compose.material3.DisappearingScaleBar
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.style.rememberStyleState

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val cameraState = rememberCameraState()
            val styleState = rememberStyleState()

            Box(modifier = Modifier.fillMaxSize()) {
                MaplibreMap(
                    cameraState = cameraState,
                    styleState = styleState,
                    options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
                )

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
                        modifier = Modifier.align(Alignment.BottomEnd),
                        contentAlignment = Alignment.BottomEnd,
                    )
                }
            }
        }
    }
}