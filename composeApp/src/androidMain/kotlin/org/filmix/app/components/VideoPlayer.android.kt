package org.filmix.app.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(modifier: Modifier, controller: VideoPlayerController) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }
    val koin = getKoin()
    val httpClient = remember { koin.get<HttpClient>() }
    val isPlaying by controller.isPlaying

    DisposableEffect(Unit) {
        setShowSystemUi(activity.window, false)

        onDispose {
            setShowSystemUi(activity.window, true)
        }
    }

    DisposableEffect(isPlaying) {
        if (isPlaying) {
            setShowSystemUi(activity.window, false)
            activity.window.decorView.keepScreenOn = true
        }

        onDispose {
            activity.window.decorView.keepScreenOn = false
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val loadControl = DefaultLoadControl
                .Builder()
                .setBufferDurationsMs(50_000, 50_000, 1_500, 5_000)
                .build()
            val mediaSourceFactory = getMediaSourceFactory(context, httpClient)
            val player = ExoPlayer
                .Builder(ctx)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .build()
            controller.setVideoPlayer(player)

            PlayerView(ctx).apply {
                useController = false
                setPlayer(player)
            }
        }
    )
}

private fun setShowSystemUi(window: Window, show: Boolean) {
    WindowCompat.setDecorFitsSystemWindows(window, show)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        if (show) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@UnstableApi
actual class VideoPlayerController actual constructor(scope: CoroutineScope) {

    private lateinit var player: ExoPlayer

    private val videoPosition = mutableStateOf(Duration.ZERO)
    private val videoBuffering = mutableStateOf(Duration.ZERO)
    private val videoDuration = mutableStateOf(Duration.ZERO)
    private val videoState = mutableStateOf(PlaybackState.BUFFERING)
    private val playing = mutableStateOf(false)

    init {
        scope.launch {
            while (true) {
                delay(500)
                videoPosition.value = player.currentPosition.milliseconds
                videoBuffering.value = player.totalBufferedDuration.milliseconds
                playing.value = player.isPlaying
            }
        }
    }

    actual val position: State<Duration> = videoPosition
    actual val buffering: State<Duration?> = videoBuffering
    actual val duration: State<Duration> = videoDuration
    actual val seekDuration: State<Duration> = derivedStateOf {
        getSeekDuration(duration.value)
    }
    actual val state: State<PlaybackState> = videoState
    actual val isPlaying: State<Boolean> = playing

    actual fun setVideoUrl(url: String, startPosition: Duration) {
        player.setMediaItem(MediaItem.fromUri(url), startPosition.inWholeMilliseconds)
        player.prepare()
    }

    actual fun play() {
        playing.value = true
        player.play()
    }

    actual fun seek(position: Duration) {
        player.seekTo(position.inWholeMilliseconds)
    }

    actual fun seekBackward() {
        val position = player.currentPosition.milliseconds - seekDuration.value
        player.seekTo(position.inWholeMilliseconds)
    }

    actual fun seekForward() {
        val position = player.currentPosition.milliseconds + seekDuration.value
        player.seekTo(position.inWholeMilliseconds)
    }

    actual fun pause() {
        playing.value = false
        player.pause()
    }

    actual fun dispose() {
        playing.value = false
        player.release()
    }

    fun setVideoPlayer(videoPlayer: ExoPlayer) {
        player = videoPlayer
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_READY -> {
                        videoDuration.value = player.duration.milliseconds
                        PlaybackState.READY
                    }

                    Player.STATE_ENDED -> PlaybackState.ENDED
                    else -> PlaybackState.BUFFERING
                }
                videoState.value = state
                playing.value = player.isPlaying
            }
        })
    }
}

@OptIn(UnstableApi::class)
private fun getMediaSourceFactory(context: Context, httpClient: HttpClient): MediaSource.Factory {
    val ktorDataSource = KtorHttpDataSource.Factory(httpClient)
    val dataSourceFactory = DefaultDataSource.Factory(context, ktorDataSource)
    return ProgressiveMediaSource.Factory(dataSourceFactory)
}

tailrec fun Context.findActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("Activity not found")
}