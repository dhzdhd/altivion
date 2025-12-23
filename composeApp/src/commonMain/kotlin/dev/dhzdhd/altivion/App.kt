package dev.dhzdhd.altivion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.DisappearingCompassButton
import org.maplibre.compose.material3.DisappearingScaleBar
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState

enum class Page {
    HOME, SEARCH
}

@Composable
@Preview
fun App() {
    val startPage = Page.HOME
    var selectedPage by rememberSaveable {
        mutableIntStateOf(startPage.ordinal)
    }

    MaterialTheme {
        Scaffold { contentPadding ->
            Column(
                modifier = Modifier.background(Color(0, 0, 0 ,0))
                    .padding(contentPadding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val cameraState = rememberCameraState()
                val styleState = rememberStyleState()
                val baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")

                Box(modifier = Modifier.fillMaxSize()) {
                    MaplibreMap(
                        baseStyle = baseStyle,
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
        }
    }
}