package dev.dhzdhd.altivion.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.dhzdhd.altivion.home.components.InteractiveMap

@Composable
fun HomeView(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier.background(Color(0, 0, 0, 0))
            .padding(contentPadding).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InteractiveMap()
    }
}