package org.filmix.app.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

const val WIDESCREEN_RATIO = 16f / 9f

@Composable
expect fun VideoPlayer(modifier: Modifier, controller: VideoPlayerController)

expect class VideoPlayerController(scope: CoroutineScope) {
    val position: State<Duration>
    val buffering: State<Duration?>
    val duration: State<Duration>
    val seekDuration: State<Duration>
    val state: State<PlaybackState>
    val isPlaying: State<Boolean>
    fun setVideoUrl(url: String, startPosition: Duration)
    fun play()
    fun seek(position: Duration)
    fun seekBackward()
    fun seekForward()
    fun pause()
    fun dispose()
}

fun getSeekDuration(duration: Duration): Duration {
    return (duration / 100)
        .coerceAtLeast(10.seconds)
        .coerceAtMost(60.seconds)
        .also {
            println("getSeekDuration ${it.inWholeSeconds}")
        }
}

enum class PlaybackState {
    BUFFERING, READY, ENDED
}