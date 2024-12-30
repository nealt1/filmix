package org.filmix.app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.Component
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Composable
actual fun VideoPlayer(modifier: Modifier, controller: VideoPlayerController) {
    val surface = remember {
        SkiaBitmapVideoSurface().also {
            controller.mediaPlayer.videoSurface().set(it)
        }
    }
    Box(modifier = modifier) {
        surface.bitmap.value?.let { bitmap ->
            Image(
                bitmap,
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
            )
        }
    }
}

actual class VideoPlayerController actual constructor(scope: CoroutineScope) {

    private val mediaPlayerComponent by lazy { initializeMediaPlayerComponent() }
    val mediaPlayer: EmbeddedMediaPlayer = mediaPlayerComponent.mediaPlayer()
    private val videoPosition = mutableStateOf(ZERO)
    private val videoBuffer = mutableStateOf(0.0)
    private val videoDuration = mutableStateOf(ZERO)
    private val videoState = mutableStateOf(PlaybackState.BUFFERING)
    private val playing = mutableStateOf(false)

    init {
        mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun lengthChanged(mediaPlayer: MediaPlayer, newLength: Long) {
                videoDuration.value = newLength.milliseconds
            }

            override fun playing(mediaPlayer: MediaPlayer) {
                videoState.value = PlaybackState.READY
                playing.value = true
            }

            override fun paused(mediaPlayer: MediaPlayer) {
                playing.value = false
            }

            override fun buffering(mediaPlayer: MediaPlayer, newCache: Float) {
                if (videoBuffer.value > newCache) {
                    videoState.value = PlaybackState.BUFFERING
                } else if (newCache > 99) {
                    videoState.value = PlaybackState.READY
                }
                videoBuffer.value = newCache * 0.01
            }

            override fun finished(mediaPlayer: MediaPlayer) {
                videoState.value = PlaybackState.ENDED
            }
        })

        scope.launch {
            while (true) {
                delay(200)
                playing.value = mediaPlayer.status().isPlaying
                videoPosition.value = mediaPlayer.status().time().milliseconds
            }
        }
    }

    actual val position: State<Duration> = videoPosition
    actual val duration: State<Duration> = videoDuration
    actual val buffering: State<Duration?> = derivedStateOf {
         networkCache.times(videoBuffer.value)
    }
    actual val state: State<PlaybackState> = videoState
    actual val isPlaying: State<Boolean> = playing

    actual suspend fun setVideoUrl(url: String, startPosition: Duration) {
        mediaPlayer.media().prepare(
            url,
            ":network-caching=${networkCache.inWholeMilliseconds}",
            ":start-time=${startPosition.toDouble(DurationUnit.SECONDS)}"
        )
    }

    actual fun play() {
        playing.value = true
        mediaPlayer.controls().play()
    }

    actual fun seek(position: Duration) {
        mediaPlayer.controls().setTime(position.inWholeMilliseconds)
    }

    actual fun seekBackward() {
        mediaPlayer.controls().skipTime(-seekDuration.inWholeMilliseconds)
    }

    actual fun seekForward() {
        mediaPlayer.controls().skipTime(seekDuration.inWholeMilliseconds)
    }

    actual fun pause() {
        playing.value = false
        mediaPlayer.controls().pause()
    }

    actual fun dispose() {
        playing.value = false
        mediaPlayer.release()
    }

    companion object {
        private val networkCache = 5.seconds
        private val seekDuration = 10.seconds
    }
}

private fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}


private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}
