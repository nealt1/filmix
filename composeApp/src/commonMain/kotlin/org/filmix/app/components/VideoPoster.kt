package org.filmix.app.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.filmix.app.models.Video
import org.lighthousegames.logging.logging

private val log = logging()

@Composable
fun VideoPoster(video: Video) {
    val posterResource = asyncPainterResource(data = video.poster)
    KamelImage(
        resource = posterResource,
        contentDescription = video.title,
        onLoading = { progress ->
            CircularProgressIndicator({ progress })
        },
        onFailure = { log.error(it) { "Failed to load image ${video.poster}" } },
        modifier = Modifier.height(313.dp).width(220.dp)
    )
}