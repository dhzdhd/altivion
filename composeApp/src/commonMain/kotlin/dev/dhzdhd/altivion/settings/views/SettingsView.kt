package dev.dhzdhd.altivion.settings.views

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
import dev.dhzdhd.altivion.settings.viewmodels.SettingsViewModel

@Composable
fun SettingsView(viewModel: SettingsViewModel, contentPadding: PaddingValues) {
    Box(
        modifier = Modifier.background(Color(0, 0, 0, 0))
            .padding(contentPadding).fillMaxSize(),
    ) {
        ElevatedButton(onClick = {
        }) {
            Text("Click")
        }
    }
}