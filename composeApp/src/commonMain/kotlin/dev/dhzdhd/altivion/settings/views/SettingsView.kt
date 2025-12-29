package dev.dhzdhd.altivion.settings.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.dhzdhd.altivion.settings.viewmodels.SettingsAction
import dev.dhzdhd.altivion.settings.viewmodels.SettingsViewModel
import dev.dhzdhd.altivion.settings.viewmodels.ThemeMode

@Composable
fun SettingsView(viewModel: SettingsViewModel, contentPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsState()

    Column (
        modifier = Modifier.background(Color(0, 0, 0, 0))
            .padding(contentPadding).fillMaxSize(),
    ) {
        ListItem(
            modifier = Modifier.clickable(true, onClick = {}),
            headlineContent = { Text("Theme", style = MaterialTheme.typography.titleLarge) },
            trailingContent = {
                SingleChoiceSegmentedButtonRow {
                    ThemeMode.entries.toTypedArray().map { themeMode ->
                        SegmentedButton(
                            selected = settings.themeMode == themeMode,
                            modifier = Modifier.padding(2.dp),
                            onClick = { viewModel.dispatch(SettingsAction.UpdateTheme(themeMode)) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(themeMode.displayName)
                        }
                    }
                }
            })
    }
}