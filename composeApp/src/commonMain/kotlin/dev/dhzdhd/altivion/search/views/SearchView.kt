package dev.dhzdhd.altivion.search.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.dhzdhd.altivion.search.viewmodels.SearchViewModel

@Composable
fun SearchView(viewModel: SearchViewModel, contentPadding: PaddingValues) {
  Box(
      modifier = Modifier.background(Color(0, 0, 0, 0)).padding(contentPadding).fillMaxSize(),
  ) {
    Column {
      OutlinedTextField(
          state = rememberTextFieldState(),
          lineLimits = TextFieldLineLimits.SingleLine,
          label = { Text("ICAO | IATA | Airport | Airplane") },
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
      LazyColumn {}
    }
  }
}
