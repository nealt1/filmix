package org.filmix.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.filmix.app.models.VideoInfo
import org.filmix.app.screens.video.VideoScreen
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieOverview(video: VideoInfo) {
    val navigator = LocalNavigator.currentOrThrow
    val posterResource = asyncPainterResource(data = video.poster)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        onClick = { navigator.push(VideoScreen(video.id)) },
        modifier = Modifier.size(width = 220.dp, height = 354.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(313.dp)
            ) {
                KamelImage(
                    resource = posterResource,
                    contentDescription = null,
                    onLoading = { progress -> CircularProgressIndicator(progress) },
                )
                val backgroundColor = if (video.rating > 0) Color.Green else Color.Red
                Text(
                    "${video.rating.absoluteValue}",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp)
                        .background(backgroundColor.copy(alpha = 0.3f))
                        .padding(8.dp)
                )
            }

            Text(
                text = "${video.year} ${video.title}",
                modifier = Modifier.padding(8.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}