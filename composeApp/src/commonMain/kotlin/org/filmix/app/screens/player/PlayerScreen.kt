package org.filmix.app.screens.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.SliderDefaults.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import filmix.composeapp.generated.resources.*
import org.filmix.app.components.*
import org.filmix.app.ui.ClickIndication
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalWindowSize
import org.filmix.app.ui.NavigationBarState
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes

data class PlayerScreen(
    private val videoKey: String,
    private val title: String,
    private val videoUrl: String,
    private val qualities: List<Int> = emptyList()
) : Screen {

    override val key = "PlayerScreen(${videoUrl})"

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isNavigationBarVisible by NavigationBarState.isVisible
        val windowSize = LocalWindowSize.current
        val screenHeight = minOf(windowSize.width, windowSize.height)
        val qualities = filterByScreenSize(qualities, screenHeight)
        val model = getScreenModel<PlayerScreenModel> {
            parametersOf(videoKey, videoUrl, qualities, screenHeight)
        }
        val player = model.player
        val url by model.url

        val playerState by player.state
        val playerFocusRequester = FocusRequester()
        val selectedQuality = model.selectedQuality

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = Color.White,
                primaryContainer = Color.Black
            )
        ) {
            LaunchedEffect(url) {
                model.play(url)
            }

            LaunchedEffect(playerState) {
                println("State $playerState")
                when (playerState) {
                    PlaybackState.BUFFERING -> {
                        model.onBuffering()
                    }

                    PlaybackState.READY -> {
                        model.onReady()
                    }

                    PlaybackState.ENDED -> {
                        navigator.pop()
                    }
                }
            }

            LaunchedEffect(player.isPlaying.value) {
                model.onChangePlaying()
            }

            DisposableEffect(Unit) {
                isNavigationBarVisible = false
                playerFocusRequester.requestFocus()

                onDispose {
                    isNavigationBarVisible = true
                    model.onExit()
                    player.dispose()
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .focusable()
                    .focusRequester(playerFocusRequester)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = ClickIndication,
                        onClick = {
                            with(player) { if (isPlaying.value) pause() else play() }
                        }
                    )
                    .onPreviewKeyEvent { event ->
                        handleKeyPress(event, navigator, model, player)
                    },
            ) {
                VideoPlayer(
                    modifier = Modifier.aspectRatio(WIDESCREEN_RATIO),
                    controller = player
                )

                AnimatedVisibility(
                    visible = player.state.value != PlaybackState.READY || !player.isPlaying.value,
                    enter = fadeIn(),
                    exit = fadeOut(
                        animationSpec = tween(2000)
                    )
                ) {
                    Controls(
                        title = title,
                        player = player,
                        selectedQuality = {
                            SelectQuality(selectedQuality, qualities) {
                                selectedQuality.value = it
                                model.onChangeQuality(it)
                            }
                        },
                        onSeek = {
                            player.seek(it)
                            model.onSeek(it)
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Controls(
        title: String,
        player: VideoPlayerController,
        selectedQuality: @Composable () -> Unit,
        onSeek: (Duration) -> Unit
    ) {
        val platform = LocalPlatform.current
        val interactionSource = remember { MutableInteractionSource() }

        Box(modifier = Modifier.fillMaxSize()) {
            VideoTitle(title)

            if (!platform.isTV) {
                CloseButton(Modifier.align(Alignment.TopStart))
            }

            when (player.state.value) {
                PlaybackState.READY -> PlayButton(player)
                PlaybackState.BUFFERING -> BufferingIcon()
                PlaybackState.ENDED -> {}
            }

            Box(Modifier.align(Alignment.TopEnd)) {
                selectedQuality()
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(80.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val videoDuration = player.duration.value
                if (videoDuration != ZERO) {
                    Slider(
                        value = player.position.value.div(videoDuration).toFloat(),
                        onValueChange = { onSeek((videoDuration.times(it.toDouble()))) },
                        modifier = Modifier.weight(1.0f),
                        track = { state ->
                            Track(state)
                        },
                        thumb = {
                            Box(
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Thumb(
                                    interactionSource = interactionSource,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                Text(
                                    text = player.position.value.toVideoTime(),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                        }
                    )

                    Text(
                        text = videoDuration.toVideoTime(),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    private fun BoxScope.VideoTitle(title: String) {
        Text(
            text = title,
            modifier = Modifier.Companion.align(Alignment.TopCenter).padding(8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    private fun CloseButton(modifier: Modifier = Modifier) {
        val navigator = LocalNavigator.currentOrThrow
        IconButton(
            onClick = { navigator.pop() },
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.action_close),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun BoxScope.PlayButton(player: VideoPlayerController) {
        val (icon, action) = if (player.isPlaying.value) {
            MaterialIcons.Pause to Res.string.action_pause
        } else {
            Icons.Default.PlayArrow to Res.string.action_play
        }

        Icon(
            imageVector = icon,
            contentDescription = stringResource(action),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center).size(100.dp)
        )
    }

    @Composable
    private fun BoxScope.BufferingIcon() {
        CircularProgressIndicator(
            Modifier.align(Alignment.Center).size(100.dp)
        )
    }

    @Composable
    private fun SelectQuality(
        selectedQuality: MutableState<Int>,
        qualities: List<Int>,
        onChangeQuality: (Int) -> Unit
    ) {
        if (qualities.size > 1) {
            var showTranslations by remember { mutableStateOf(false) }

            TextButton(onClick = { showTranslations = true }) {
                SelectedQuality(selectedQuality.value)

                DropdownMenu(
                    expanded = showTranslations,
                    onDismissRequest = { showTranslations = false },
                    modifier = Modifier.sizeIn(maxWidth = 68.dp)
                ) {
                    qualities.forEach {
                        DropdownMenuItem(
                            text = { SelectedQuality(it) },
                            onClick = { onChangeQuality(it) }
                        )
                    }
                }
            }
        } else {
            SelectedQuality(
                quality = selectedQuality.value,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun SelectedQuality(
        quality: Int,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified
    ) {
        val text = VIDEO_QUALITY[quality] ?: "${quality}p"
        Text(text, softWrap = false, modifier = modifier, color = color)
    }

    private fun Duration.toVideoTime(): String {
        return toComponents { days, hours, minutes, seconds, _ ->
            if (days > 0) {
                "$days:${digit(hours)}:${digit(minutes)}:${digit(seconds)}"
            } else if (hours > 0) {
                "$hours:${digit(minutes)}:${digit(seconds)}"
            } else if (minutes > 0) {
                "$minutes:${digit(seconds)}"
            } else {
                "0:${digit(seconds)}"
            }
        }
    }

    private fun digit(hours: Int) = hours.toString().padStart(2, '0')

    private fun filterByScreenSize(qualities: List<Int>, screenHeight: Int): List<Int> {
        return qualities.filter { it <= screenHeight }
            .ifEmpty {
                listOfNotNull(qualities.lastOrNull() ?: 720)
            }
    }

    private fun handleKeyPress(
        event: KeyEvent,
        navigator: Navigator,
        model: PlayerScreenModel,
        player: VideoPlayerController
    ): Boolean {
        if (event.type != KeyEventType.KeyDown) {
            return false
        }
        println("Player key event $event")
        return when (event.key) {
            KEYCODE_BACK -> {
                if ((player.duration.value - player.position.value) < 1.minutes) {
                    model.onComplete()
                }
                navigator.pop()
                true
            }

            KEYCODE_ARROW_LEFT -> {
                player.seekBackward()
                model.onSeek(player.position.value)
                true
            }

            KEYCODE_ARROW_RIGHT -> {
                player.seekForward()
                model.onSeek(player.position.value)
                true
            }

            KEYCODE_DPAD_LEFT -> {
                player.seekBackward()
                model.onSeek(player.position.value)
                true
            }

            KEYCODE_DPAD_RIGHT -> {
                player.seekForward()
                model.onSeek(player.position.value)
                true
            }

            else -> false
        }
    }

    @Composable
    fun Thumb(
        interactionSource: MutableInteractionSource,
        modifier: Modifier = Modifier,
        colors: SliderColors = colors(),
    ) {
        Spacer(
            modifier
                .size(DpSize(16.dp, 16.dp))
                .hoverable(interactionSource = interactionSource)
                .background(colors.thumbColor, CircleShape)
                .border(0.5.dp, colors.disabledThumbColor, CircleShape)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Track(sliderState: SliderState) {
        SliderDefaults.Track(
            sliderState = sliderState,
            thumbTrackGapSize = 0.dp,
            modifier = Modifier.height(8.dp)
        )
    }

    companion object {
        private val KEYCODE_BACK = Key(0x00000004)
        private val KEYCODE_DPAD_LEFT = Key(0x00000015)
        private val KEYCODE_DPAD_RIGHT = Key(0x00000016)
        private val KEYCODE_ARROW_LEFT = Key(0x00000025)
        private val KEYCODE_ARROW_RIGHT = Key(0x00000027)
        private val VIDEO_QUALITY = mapOf(
            480 to "SD",
            720 to "HD",
            1080 to "Full HD",
            1440 to "Full HD+",
            2160 to "4K",
        )
    }
}