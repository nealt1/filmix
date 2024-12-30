package org.filmix.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.kamel.image.asyncPainterResource
import org.filmix.app.models.Video
import org.filmix.app.models.VideoInfo
import org.filmix.app.screens.video.VideoScreen
import kotlin.math.absoluteValue

@Composable
fun MovieOverview(video: Video, modifier: Modifier = Modifier) {
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
        shape = RectangleShape,
        modifier = modifier.width(220.dp).height(340.dp)
            .onFocusChanged { isCardFocused = it.isFocused }
            .hoverable(interactionSource = hoverInteractionSource)
            .then(
                if (isCardFocused || isCardHovered) {
                    Modifier.shadow(
                        spotColor = MaterialTheme.colorScheme.primary,
                        elevation = 12.dp
                    ).border(1.dp, MaterialTheme.colorScheme.primary)
                } else Modifier
            )
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(313.dp)
            ) {
                VideoPoster(video)

                if (video is VideoInfo) {
                    val backgroundColor = if (video.rating > 0) Color.Green else Color.Red
                    Text(
                        "${video.rating.absoluteValue}",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                            .background(backgroundColor.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            Row(modifier = Modifier.height(26.dp)) {
                Text(
                    text = "${video.year} ${video.title}",
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}