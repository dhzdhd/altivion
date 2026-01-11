package dev.dhzdhd.altivion.settings.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.dhzdhd.altivion.settings.viewmodels.MapStyle
import dev.dhzdhd.altivion.settings.viewmodels.SettingsAction
import dev.dhzdhd.altivion.settings.viewmodels.SettingsViewModel
import dev.dhzdhd.altivion.settings.viewmodels.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(viewModel: SettingsViewModel, contentPadding: PaddingValues) {
  val settings by viewModel.settings.collectAsState()

  Column(
      modifier = Modifier.background(Color(0, 0, 0, 0)).padding(contentPadding).fillMaxSize(),
  ) {
    ListItem(
        headlineContent = { Text("Theme", style = MaterialTheme.typography.titleMedium) },
        trailingContent = {
          SingleChoiceSegmentedButtonRow {
            ThemeMode.entries.toTypedArray().map { themeMode ->
              SegmentedButton(
                  selected = settings.themeMode == themeMode,
                  modifier = Modifier.padding(2.dp),
                  onClick = { viewModel.dispatch(SettingsAction.UpdateTheme(themeMode)) },
                  shape = RoundedCornerShape(10.dp)) {
                    Text(themeMode.displayName)
                  }
            }
          }
        })
    ListItem(
        headlineContent = { Text("Light Map Style", style = MaterialTheme.typography.titleMedium) },
        trailingContent = {
          var expanded by remember { mutableStateOf(false) }

          TooltipBox(
              positionProvider =
                  TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
              tooltip = { PlainTooltip { Text("Light mode map style") } },
              state = rememberTooltipState(),
          ) {
            TextButton(onClick = { expanded = true }) { Text(settings.lightMapStyle.displayName) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              MapStyle.entries.map {
                DropdownMenuItem(
                    text = { Text(it.displayName) },
                    onClick = {
                      viewModel.dispatch(SettingsAction.UpdateLightMapStyle(it))
                      expanded = false
                    },
                )
              }
            }
          }
        })
    ListItem(
        headlineContent = { Text("Dark Map Style", style = MaterialTheme.typography.titleMedium) },
        trailingContent = {
          var expanded by remember { mutableStateOf(false) }

          TooltipBox(
              positionProvider =
                  TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
              tooltip = { PlainTooltip { Text("Dark mode map style") } },
              state = rememberTooltipState(),
          ) {
            TextButton(onClick = { expanded = true }) { Text(settings.darkMapStyle.displayName) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              MapStyle.entries.map {
                DropdownMenuItem(
                    text = { Text(it.displayName) },
                    onClick = {
                      viewModel.dispatch(SettingsAction.UpdateDarkMapStyle(it))
                      expanded = false
                    },
                )
              }
            }
          }
        })
      HorizontalDivider()
  }
}
