package dev.dhzdhd.altivion.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.dhzdhd.altivion.home.components.InteractiveMap
import dev.dhzdhd.altivion.home.viewmodels.HomeViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.dhzdhd.altivion.settings.viewmodels.SettingsState
import dev.dhzdhd.altivion.settings.viewmodels.ThemeMode

@Composable
fun HomeView(viewModel: HomeViewModel, settings: SettingsState, contentPadding: PaddingValues) {
    val mapStyle = when (settings.themeMode) {
        ThemeMode.Light -> settings.lightMapStyle
        ThemeMode.Dark -> settings.darkMapStyle
    }

    Box(
        modifier = Modifier.background(Color(0, 0, 0, 0))
            .padding(contentPadding).fillMaxSize(),
    ) {
        InteractiveMap(viewModel, mapStyle)
    }
}