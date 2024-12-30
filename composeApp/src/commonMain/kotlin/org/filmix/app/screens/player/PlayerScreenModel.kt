package org.filmix.app.screens.player

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.filmix.app.components.VideoPlayerController
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class PlayerScreenModel(
    factory: Settings.Factory,
    videoId: Int,
    private val videoUrl: String,
    private val qualities: List<Int>,
    private val screenHeight: Int
) : ScreenModel {
    private val videoSettings = factory.create("video-$videoId")
    private val playbackTime = mutableMapOf<Int, Duration?>()
    private val playerQualityChange = mutableStateOf(Clock.System.now())
    private val playerSeek = mutableStateOf(Clock.System.now())

    val player = VideoPlayerController(screenModelScope)
    val selectedQuality = mutableStateOf(getVideoQuality())
    val url = derivedStateOf {
        videoUrl.replace("%s", selectedQuality.value.toString())
    }

    suspend fun play(url: String) {
        val currentPosition = getVideoPosition()
        println("Launch $url seek to ${currentPosition.toString(DurationUnit.SECONDS)}")

        player.setVideoUrl(url, currentPosition)
        playerSeek.value = Clock.System.now()

        player.play()

        getBetterQuality(selectedQuality.value)?.let { betterQuality ->
            screenModelScope.launch {
                delay(MIN_DURATION_FOR_QUALITY_INCREASE)
                println("Upgrading quality to $betterQuality")
                selectedQuality.value = betterQuality
                saveVideoQuality(betterQuality)
            }
        }
    }

    private fun getBetterQuality(selectedQuality: Int): Int? {
        val qualityIndex = qualities.indexOf(selectedQuality)
        return if (qualityIndex > 0) {
            qualities[qualityIndex - 1].takeIf {
                val lastPlayTime = playbackTime[it]
                it <= screenHeight && (lastPlayTime == null || lastPlayTime > MIN_DURATION_FOR_QUALITY_INCREASE)
            }
        } else null
    }

    fun onBuffering() {
        val now = Clock.System.now()
        val lastSeekTime = now - playerSeek.value
        val playTime = now - playerQualityChange.value

        println("Buffering, last seek $lastSeekTime, play time $playTime, ${playerQualityChange.value}")

        if (playTime > MIN_DURATION_FOR_QUALITY_DECREASE &&
            lastSeekTime > MIN_DURATION_FOR_QUALITY_DECREASE
        ) {
            qualities.firstOrNull { it < selectedQuality.value }
                ?.let { lowerQuality ->
                    println("Downgrading to quality $lowerQuality")
                    playbackTime[selectedQuality.value] = playTime
                    selectedQuality.value = lowerQuality
                    saveVideoQuality(lowerQuality)
                }
        }
    }

    fun onChangeQuality(quality: Int) {
        playerQualityChange.value = Clock.System.now()
        println("Player quality $quality")
        saveVideoPosition(player.position.value)
        saveVideoQuality(quality)
    }

    fun onSeek(position: Duration) {
        println("PlayerScreenModel#onSeek($position)")
        playerSeek.value = Clock.System.now()
        player.seek(position)
        saveVideoPosition(position)
    }

    fun onChangePlaying() {
        println("PlayerScreenModel#onChangePlaying()")
        playerSeek.value = Clock.System.now()
        saveVideoPosition(player.position.value)
    }

    fun onExit() {
        println("PlayerScreenModel#onExit()")
        saveVideoPosition(player.position.value)
    }

    private fun getVideoQuality(): Int {
        return videoSettings.get<Int>("quality")?.let { quality ->
            qualities.firstOrNull { it == quality }
        } ?: qualities.firstOrNull { it <= screenHeight } ?: 0
    }

    private fun saveVideoQuality(quality: Int) {
        println("PlayerScreenModel#saveVideoQuality($quality)")
        return videoSettings.putInt("quality", quality)
    }

    private fun getVideoPosition(): Duration {
        return videoSettings.get<Long>("position")?.seconds
            ?: player.position.value
    }

    private fun saveVideoPosition(position: Duration) {
        videoSettings.putLong("position", position.inWholeSeconds)
    }

    companion object {
        private val MIN_DURATION_FOR_QUALITY_INCREASE = 5.minutes
        private val MIN_DURATION_FOR_QUALITY_DECREASE = 25.seconds
    }
}