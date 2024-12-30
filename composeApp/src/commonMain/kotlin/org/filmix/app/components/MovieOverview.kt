package org.filmix.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
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

@Composable
fun MovieOverview(video: VideoInfo) {
    val navigator = LocalNavigator.currentOrThrow
    val posterResource = asyncPainterResource(data = video.poster)
    var isCardFocused by remember { mutableStateOf(false) }
    var isCardHovered by remember { mutableStateOf(false) }
    val hoverInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(hoverInteractionSource) {
        hoverInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> {
                    isCardHovered = true
                }
                is HoverInteraction.Exit -> {
                    isCardHovered = false
                }
            }
        }
    }

    Card(
        onClick = { navigator.push(VideoScreen(video.id)) },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .onFocusChanged { isCardFocused = it.isFocused }
            .hoverable(interactionSource = hoverInteractionSource)
            .then(if (isCardFocused || isCardHovered) {
                Modifier.shadow(
                    spotColor = MaterialTheme.colorScheme.primary,
                    elevation = 12.dp
                )
            } else Modifier)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(313.dp)
            ) {
                KamelImage(
                    resource = posterResource,
                    contentDescription = null,
                    onLoading = { progress ->
                        CircularProgressIndicator(
                            progress = { progress },
                        )
                    }
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
                modifier = Modifier.padding(8.dp).height(24.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}