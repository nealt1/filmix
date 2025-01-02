package org.filmix.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.discardRemaining
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.filmix.app.Platform
import org.filmix.app.app.Preferences
import org.filmix.app.models.MovieSection
import org.filmix.app.models.TokenRequest
import org.filmix.app.models.UpdateInfo
import org.filmix.app.models.UserProfile
import org.filmix.app.models.VideoData
import org.filmix.app.models.VideoDetails
import org.filmix.app.models.WatchedVideoData
import org.filmix.app.paging.IntPage
import org.lighthousegames.logging.logging

class VideoRepository(
    private val httpClient: HttpClient,
    private val fileCache: FileCache,
    private val preferences: Preferences,
    private val platform: Platform
) {
    private val baseUrl = "http://filmixapp.cyou"

    suspend fun requestToken(): TokenRequest {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/token_request")
            .build()

        log.debug { "requestToken()" }

        return withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().body<TokenRequest>()
        }
    }

    suspend fun checkUpdate(): UpdateInfo {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/check_update")
            .apply { parameters.appendAll(getParameters()) }
            .build()

        log.debug { "checkUpdate()" }

        return getCachedResponse<UpdateInfo>(requestUrl)
    }

    suspend fun getCatalog(
        page: Int? = null,
        section: MovieSection
    ): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/catalog")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("page", currentPage.toString())
                parameters.append("filter", "s${section.id}")
            }
            .build()

        log.debug { "getCatalog(page=$page,section=${section.name})" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "getCatalog ${videos.size}" }

        return videos.toIntPage(currentPage)
    }

    suspend fun getFavourite(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/favourites")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("page", currentPage.toString())
            }
            .build()

        log.debug { "getFavourite(page=$page)" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "getFavourite ${videos.size}" }

        return videos.toIntPage(currentPage)
    }

    suspend fun getHistory(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1

        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/history")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("page", currentPage.toString())
            }
            .build()

        log.debug { "getHistory(page=$page)" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "getHistory ${videos.size}" }

        return videos.toIntPage(currentPage)
    }

    suspend fun clearHistory() {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/history_clean")
            .apply { parameters.appendAll(getParameters()) }
            .buildString()

        log.debug { "cleanHistory()" }

        withContext(Dispatchers.IO) {
            httpClient.get(requestUrl).validate().discardRemaining()
        }
    }

    suspend fun getPopular(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/popular")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("page", currentPage.toString())
            }
            .build()

        log.debug { "getPopular(page=$page)" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "getPopular ${videos.size}" }

        return videos.toIntPage(currentPage)
    }

    suspend fun getTrending(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/top_views")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("page", currentPage.toString())
            }
            .build()

        log.debug { "getTrending(page=$page)" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "getTrending ${videos.size}" }

        return videos.toIntPage(currentPage)
    }

    suspend fun getSaved(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/deferred")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("page", currentPage.toString())
            }
            .build()

        log.debug { "getSaved(page=$page)" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "getSaved ${videos.size}" }

        return videos.toIntPage(currentPage)
    }

    suspend fun search(query: String, page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1

        if (query.isBlank()) {
            return emptyList<VideoData>().toIntPage(currentPage)
        }

        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/search")
            .apply {
                parameters.appendAll(getParameters())
                parameters.append("story", query)
                parameters.append("page", currentPage.toString())
            }
            .build()

        log.debug { "search(query=$query,page=$page)" }

        val videos = getCachedResponse<List<VideoData>>(requestUrl)

        log.debug { "search ${videos.size}" }

        return videos.toIntPage(currentPage, pageSize = 100)
    }

    suspend fun toggleFavourite(videoId: Int) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/toggle_fav/$videoId")
            .apply { parameters.appendAll(getParameters()) }
            .buildString()

        log.debug { "toggleFavourite($videoId)" }

        withContext(Dispatchers.IO) {
            httpClient.get(requestUrl).validate().discardRemaining()
        }
    }

    suspend fun toggleSaved(videoId: Int) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/toggle_wl/$videoId")
            .apply { parameters.appendAll(getParameters()) }
            .buildString()

        log.debug { "toggleFavourite($videoId)" }

        withContext(Dispatchers.IO) {
            httpClient.get(requestUrl).validate().discardRemaining()
        }
    }

    suspend fun addWatched(videoId: Int, details: WatchedVideoData) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/add_watched")
            .apply { parameters.appendAll(getParameters()) }
            .buildString()

        log.debug { "setWatched($videoId)" }

        withContext(Dispatchers.IO) {
            httpClient.submitForm(
                url = requestUrl,
                formParameters = parameters {
                    append("id", videoId.toString())
                    append("add_watched", "true")
                    details.translation?.let { append("translation", it) }
                    details.season?.let { append("season", it) }
                    details.episode?.let { append("episode", it) }
                }
            ).validate().discardRemaining()
        }
    }

    suspend fun getVideo(id: Int): VideoDetails {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/post/$id")
            .apply { parameters.appendAll(getParameters()) }
            .build()

        log.debug { "getVideo($id)" }

        return getCachedResponse(requestUrl)
    }

    suspend fun getUserProfile(token: String? = null): UserProfile {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/user_profile")
            .apply { parameters.appendAll(getParameters(token)) }
            .build()

        log.debug { "getUserProfile()" }

        return withContext(Dispatchers.IO) {
            httpClient.get(requestUrl).validate().body()
        }
    }

    suspend fun setVideoServer(server: String) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/change_server")
            .apply { parameters.appendAll(getParameters()) }
            .buildString()

        log.debug { "setVideoServer($server)" }

        withContext(Dispatchers.IO) {
            httpClient.submitForm(
                url = requestUrl,
                formParameters = parameters {
                    append("vs_schg", server)
                }
            ).validate().discardRemaining()
        }
    }

    private fun List<VideoData>.toIntPage(currentPage: Int, pageSize: Int = 50) = IntPage(
        items = this,
        next = (currentPage + 1).takeIf { size == pageSize }
    )

    private fun getParameters(token: String? = null) = parameters {
        append("user_dev_apk", "2.2.0")
        append("user_dev_id", preferences.deviceId)
        append("user_dev_name", platform.deviceName)
        append("user_dev_os", platform.osVersion)
        append("user_dev_vendor", platform.vendorName)

        val devToken = token ?: preferences.getToken()
        devToken?.let { token ->
            append("user_dev_token", token)
        }
    }

    private suspend inline fun <reified T> getCachedResponse(requestUrl: Url): T {
        return withContext(Dispatchers.IO) {
            val responseBody = fileCache.getOrPut(requestUrl.toString()) {
                httpClient.get(requestUrl).validate().bodyAsText()
            }
            json.decodeFromString<T>(responseBody)
        }
    }

    companion object {
        private val log = logging()
        private val json = Json { ignoreUnknownKeys = true }
    }
}

private suspend fun HttpResponse.validate(): HttpResponse {
    if (!status.isSuccess()) {
        throw IOException("Failed to execute request, received HTTP ${status.value}: ${bodyAsText()}")
    }
    return this
}
