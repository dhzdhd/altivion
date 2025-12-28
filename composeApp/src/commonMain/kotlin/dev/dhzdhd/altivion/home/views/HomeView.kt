package dev.dhzdhd.altivion.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.dhzdhd.altivion.home.components.InteractiveMap
import dev.dhzdhd.altivion.home.viewmodels.HomeAction
import dev.dhzdhd.altivion.home.viewmodels.HomeViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HomeView(viewModel: HomeViewModel, contentPadding: PaddingValues) {
    val airplanes by viewModel.airplanes.collectAsState()

    Box(
        modifier = Modifier.background(Color(0, 0, 0, 0))
            .padding(contentPadding).fillMaxSize(),
    ) {
        InteractiveMap(airplanes)
        ElevatedButton(onClick = {
            viewModel.dispatch(HomeAction.GetAllItems)
        }) {
            Text("Click")
        }
    }
}