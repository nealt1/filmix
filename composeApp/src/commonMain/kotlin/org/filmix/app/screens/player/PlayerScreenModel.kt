package org.filmix.app.screens.player

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.filmix.app.components.VideoPlayerController
import org.lighthousegames.logging.logging
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class PlayerScreenModel(
    factory: Settings.Factory,
    videoKey: String,
    private val videoUrl: String,
    private val qualities: List<Int>,
    private val screenHeight: Int
) : ScreenModel {
    private val videoSettings = factory.create(videoKey)
    private val playbackTime = mutableMapOf<Int, Duration?>()
    private var playerQualityChange = Clock.System.now()
    private var playerSeek = Clock.System.now()
    private var downgradeQualityJob: Job? = null

    val player = VideoPlayerController(screenModelScope)
    val selectedQuality = mutableStateOf(getVideoQuality().also {
        log.debug { "Using video quality $it/$qualities" }
    })
    val url = derivedStateOf {
        videoUrl.replace("%s", selectedQuality.value.toString())
    }

    fun play(url: String) {
        val currentPosition = getVideoPosition()
        log.debug { "Launch $url, seek to ${currentPosition.toString(DurationUnit.SECONDS)}" }

        player.setVideoUrl(url, currentPosition)
        playerSeek = Clock.System.now()

        player.play()

        getBetterQuality(selectedQuality.value)?.let { betterQuality ->
            screenModelScope.launch {
                delay(MIN_DURATION_FOR_QUALITY_INCREASE)
                log.info { "Upgrading quality to $betterQuality" }
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
        val lastSeekTime = now - playerSeek
        val playTime = now - playerQualityChange

        log.debug { "Buffering, last seek $lastSeekTime, play time $playTime, $playerQualityChange" }

        val lowerQuality = qualities.firstOrNull { it < selectedQuality.value } ?: return

        if (playTime > MIN_DURATION_FOR_QUALITY_DECREASE &&
            lastSeekTime > MIN_DURATION_FOR_QUALITY_DECREASE
        ) {
            log.info { "Downgrading to quality $lowerQuality" }
            downgradeQuality(playTime, lowerQuality)
        } else {
            downgradeQualityJob = screenModelScope.launch {
                delay(MAX_DURATION_FOR_BUFFERING)
                log.info { "Downgrading to quality $lowerQuality due to slow buffering" }
                downgradeQuality(playTime, lowerQuality)
            }
        }
    }

    fun onReady() {
        downgradeQualityJob?.cancel()
        downgradeQualityJob = null
    }

    fun onComplete() {
        log.debug { "onComplete()" }
        videoSettings.putBoolean("completed", true)
    }

    fun onChangeQuality(quality: Int) {
        playerQualityChange = Clock.System.now()
        log.debug { "onChangeQuality($quality)" }
        saveVideoPosition(player.position.value)
        saveVideoQuality(quality)
    }

    fun onSeek(position: Duration) {
        log.debug { "onSeek($position)" }
        playerSeek = Clock.System.now()
        saveVideoPosition(position)
    }

    fun onChangePlaying() {
        log.debug { "onChangePlaying()" }
        playerSeek = Clock.System.now()
        saveVideoPosition(player.position.value)
    }

    fun onExit() {
        log.debug { "onExit()" }
        saveVideoPosition(player.position.value)
    }

    private fun getVideoQuality(): Int {
        return videoSettings.get<Int>("quality")?.let { quality ->
            qualities.firstOrNull { it == quality }
        } ?: qualities.firstOrNull() ?: 0
    }

    private fun saveVideoQuality(quality: Int) {
        log.debug { "saveVideoQuality($quality)" }
        return videoSettings.putInt("quality", quality)
    }

    private fun getVideoPosition(): Duration {
        return videoSettings.get<Long>("position")?.seconds
            ?: player.position.value
    }

    private fun saveVideoPosition(position: Duration) {
        videoSettings.putLong("position", position.inWholeSeconds)
    }

    private fun downgradeQuality(playTime: Duration, lowerQuality: Int) {
        playbackTime[selectedQuality.value] = playTime
        selectedQuality.value = lowerQuality
        saveVideoQuality(lowerQuality)
    }

    companion object {
        private val log = logging()
        private val MIN_DURATION_FOR_QUALITY_INCREASE = 5.minutes
        private val MIN_DURATION_FOR_QUALITY_DECREASE = 25.seconds
        private val MAX_DURATION_FOR_BUFFERING = 10.seconds
    }
}