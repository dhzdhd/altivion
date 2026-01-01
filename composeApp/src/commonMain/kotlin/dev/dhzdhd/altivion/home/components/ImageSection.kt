package dev.dhzdhd.altivion.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.repositories.AirplaneImage

@Composable
fun ImageSection(airplaneImage: Value<AirplaneImage>) {
    Box(
        modifier = Modifier.fillMaxWidth().height(250.dp).border(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary),
            shape = RoundedCornerShape(20.dp)
        )
    ) {
        when (airplaneImage) {
            is Value.Data -> {
                SubcomposeAsyncImage(
                    ImageRequest.Builder(context = LocalPlatformContext.current)
                        .data(airplaneImage.data.image).crossfade(true).build(),
                    contentDescription = airplaneImage.data.link,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.FillBounds,
                )
            }

            is Value.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center).size(24.dp)
            )

            is Value.Error -> Text(
                airplaneImage.error.message, modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}