package org.filmix.app.screens.video

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import io.ktor.http.URLBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.filmix.app.Platform
import org.filmix.app.data.DownloadState
import org.filmix.app.data.Downloader
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.WatchedVideoData
import org.filmix.app.state.LoadingValue
import org.filmix.app.state.collectLoaded
import org.filmix.app.state.load
import org.lighthousegames.logging.logging

class VideoScreenModel(
    private val repository: VideoRepository,
    downloader: Downloader,
    private val factory: Settings.Factory,
    private val videoId: Int
) : ScreenModel {

    private val videoDetailsFlow = snapshotFlow { videoId }
        .load {
            try {
                repository.getVideo(it).also {
                    log.debug { "Video details $videoId: $it" }
                }
            } catch (e: Exception) {
                log.error(e) { "Failed to open video $videoId" }
                throw e
            }
        }

    val videoDetails = videoDetailsFlow
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoadingValue.Loading
        )

    val videoSettings = factory.create("video-$videoId")

    var favourite = mutableStateOf(false)
    var saved = mutableStateOf(false)
    val download = Download(screenModelScope, downloader, videoSettings)

    init {
        screenModelScope.launch {
            videoDetailsFlow.collectLoaded { details ->
                favourite.value = details.favorited
                saved.value = details.watch_later
            }
        }
    }

    fun toggleFavourite() {
        favourite.value = !favourite.value
        screenModelScope.launch { repository.toggleFavourite(videoId) }
    }

    fun toggleSaved() {
        saved.value = !saved.value
        screenModelScope.launch { repository.toggleSaved(videoId) }
    }

    fun saveWatched(details: WatchedVideoData) {
        screenModelScope.launch { repository.addWatched(videoId, details) }
    }

    fun getVideoId(videoId: Int): String {
        return "video-$videoId"
    }

    fun getEpisodeId(videoId: Int, season: String, episode: String): String {
        return "episode-$videoId-$season-$episode"
    }

    fun wasEpisodeWatched(episodeId: String): Boolean {
        val episodeSettings = factory.create(episodeId)
        return episodeSettings.get("completed", false)
    }

    class Download(
        private val coroutineScope: CoroutineScope,
        private val downloader: Downloader,
        private val settings: Settings,
    ) {
        val videoUrl = mutableStateOf(
            value = settings.get<String>(DOWNLOAD_PATH)
                ?.takeIf { SystemFileSystem.exists(Path(it)) }
        )

        val downloadState = mutableStateOf(
            if (videoUrl.value == null) DownloadState.None else DownloadState.Success
        )

        private var downloadJob: Job? = null

        fun download(platform: Platform, link: VideoLink, screenHeight: Int) {
            val requiredHeight = minOf(screenHeight, 720)
            val quality = link.quality.firstOrNull { it <= requiredHeight } ?: link.quality.last()
            val url = link.url.replace("%s", quality.toString())
            val fileName = URLBuilder(url).pathSegments.last()
            val filePath = Path(platform.downloadDir, fileName)

            log.info { "Downloading video in quality $quality" }

            downloadJob = coroutineScope.launch {
                try {
                    downloader.downloadFile(url, filePath) {
                        downloadState.value = it
                    }
                } catch (e: CancellationException) {
                    SystemFileSystem.delete(filePath)
                }

                settings.putString(DOWNLOAD_PATH, filePath.toString())
                videoUrl.value = filePath.toString()
            }
        }

        fun cancel() {
            downloadJob?.cancel()
            downloadState.value = DownloadState.None
        }

        fun delete() {
            videoUrl.value?.let { filePath ->
                SystemFileSystem.delete(Path(filePath), mustExist = false)
            }
            videoUrl.value = null
            downloadState.value = DownloadState.None
        }
    }

    companion object {
        private val log = logging()
        const val DOWNLOAD_PATH = "downloadPath"
    }
}