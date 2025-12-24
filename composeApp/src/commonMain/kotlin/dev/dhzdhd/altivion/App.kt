package dev.dhzdhd.altivion

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.compose_multiplatform
import altivion.composeapp.generated.resources.home
import altivion.composeapp.generated.resources.search
import altivion.composeapp.generated.resources.settings
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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import dev.dhzdhd.altivion.home.components.InteractiveMap
import dev.dhzdhd.altivion.home.views.HomeView
import org.jetbrains.compose.resources.painterResource
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
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = false, onClick = {}, icon = {
                        Icon(
                            painter = painterResource(Res.drawable.home),
                            contentDescription = "Home",
                        )
                    }, label = { Text("Home") })
                    NavigationBarItem(selected = false, onClick = {}, icon = {
                        Icon(
                            painter = painterResource(Res.drawable.search),
                            contentDescription = "Search",
                        )
                    }, label = { Text("Search") })
                    NavigationBarItem(selected = false, onClick = {}, icon = {
                        Icon(
                            painter = painterResource(Res.drawable.settings),
                            contentDescription = "Settings",
                        )
                    }, label = { Text("Settings") })
                }
            }
        ) { contentPadding ->
            HomeView(contentPadding = contentPadding)
        }
    }
}