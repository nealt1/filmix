package org.filmix.app.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(modifier: Modifier, controller: VideoPlayerController) {
    val avPlayerViewController = remember { AVPlayerViewController() }
    val playerLayer = remember { AVPlayerLayer() }

    avPlayerViewController.player = controller.player
    avPlayerViewController.showsPlaybackControls = false
    playerLayer.player = controller.player

    UIKitView(
        factory = {
            // Create a UIView to hold the AVPlayerLayer
            UIView().apply {
                addSubview(avPlayerViewController.view)
            }
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            playerLayer.setFrame(rect)
            avPlayerViewController.view.layer.frame = rect
            CATransaction.commit()
        },
        update = { _ ->
            // play?
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalForeignApi::class)
actual class VideoPlayerController actual constructor(scope: CoroutineScope) {

    val player = AVPlayer()

    private val videoPosition = mutableStateOf(Duration.ZERO)
    private val videoDuration = mutableStateOf(Duration.ZERO)
    private val videoState = mutableStateOf(PlaybackState.BUFFERING)
    private val playing = mutableStateOf(false)

    actual val position: State<Duration> = videoPosition
    actual val buffering: State<Duration?> = mutableStateOf(null)
    actual val duration: State<Duration> = videoDuration
    actual val seekDuration: State<Duration> = derivedStateOf {
        getSeekDuration(duration.value)
    }
    actual val state: State<PlaybackState> = videoState
    actual val isPlaying: State<Boolean> = playing

    init {
        scope.launch {
            while (true) {
                delay(500)
                player.currentItem?.let { item ->
                    videoPosition.value = CMTimeGetSeconds(item.currentTime()).seconds
                    playing.value = player.timeControlStatus() == AVPlayerTimeControlStatusPlaying
                }
            }
        }
    }

    actual fun setVideoUrl(url: String, startPosition: Duration) = runBlocking(Dispatchers.IO) {
        val avPlayerItem = AVPlayerItem(uRL = NSURL.URLWithString(url)!!)
        player.replaceCurrentItemWithPlayerItem(avPlayerItem)
        while (avPlayerItem.status != AVPlayerItemStatusReadyToPlay) {
            delay(50)
        }
        videoDuration.value = CMTimeGetSeconds(avPlayerItem.duration).seconds
        seek(startPosition)
    }

    actual fun play() {
        player.play()
    }

    actual fun seek(position: Duration) {
        val seekTime = CMTimeMake(position.inWholeMilliseconds, 1_000)
        player.seekToTime(seekTime)
    }

    actual fun seekBackward() {
        val currentTime = CMTimeGetSeconds(player.currentTime()).seconds - seekDuration.value
        val seekTime = CMTimeMake(currentTime.inWholeMilliseconds, 1_000)
        player.seekToTime(seekTime)
    }

    actual fun seekForward() {
        val currentTime = CMTimeGetSeconds(player.currentTime()).seconds + seekDuration.value
        val seekTime = CMTimeMake(currentTime.inWholeMilliseconds, 1_000)
        player.seekToTime(seekTime)
    }

    actual fun pause() {
        player.pause()
    }

    actual fun dispose() {
        player.pause()
    }
}