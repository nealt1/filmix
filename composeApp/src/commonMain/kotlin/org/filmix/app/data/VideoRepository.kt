package org.filmix.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.discardRemaining
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.filmix.app.models.MovieSection
import org.filmix.app.models.TokenRequest
import org.filmix.app.models.UpdateInfo
import org.filmix.app.models.UserProfile
import org.filmix.app.models.VideoData
import org.filmix.app.models.VideoDetails
import org.filmix.app.models.WatchedVideoData
import org.filmix.app.paging.IntPage
import org.filmix.app.app.Preferences

class VideoRepository(
    private val httpClient: HttpClient,
    private val preferences: Preferences
) {
    private val baseUrl = "http://filmixapp.cyou"

    suspend fun requestToken(): TokenRequest {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/token_request")
            .build()

        println("VideoRepository#requestToken()")

        return withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().body<TokenRequest>()
        }
    }

    suspend fun checkUpdate(): UpdateInfo {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/check_update")
            .build()

        println("VideoRepository#checkUpdate()")

        return withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().body<UpdateInfo>()
        }
    }

    suspend fun getCatalog(
        page: Int? = null,
        section: MovieSection
    ): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/catalog")
            .build()

        println("VideoRepository#getCatalog(page=$page,section=${section.name})")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("page", currentPage.toString())
                url.parameters.append("filter", "s${section.id}")
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#getCatalog ${videos.size}")

        return videos.toIntPage(currentPage)
    }

    suspend fun getFavourite(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/favourites")
            .build()

        println("VideoRepository#getFavourite(page=$page)")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("page", currentPage.toString())
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#getFavourite ${videos.size}")

        return videos.toIntPage(currentPage)
    }

    suspend fun getHistory(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1

        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/history")
            .build()

        println("VideoRepository#getHistory(page=$page)")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("page", currentPage.toString())
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#getHistory ${videos.size}")

        return videos.toIntPage(currentPage)
    }

    suspend fun clearHistory() {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/history_clean")
            .build()

        println("VideoRepository#cleanHistory()")

        withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().discardRemaining()
        }
    }

    suspend fun getPopular(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/popular")
            .build()

        println("VideoRepository#getPopular(page=$page)")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("page", currentPage.toString())
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#getPopular ${videos.size}")

        return videos.toIntPage(currentPage)
    }

    suspend fun getTrending(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/top_views")
            .build()

        println("VideoRepository#getTrending(page=$page)")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("page", currentPage.toString())
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#getTrending ${videos.size}")

        return videos.toIntPage(currentPage)
    }

    suspend fun getSaved(page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/deferred")
            .build()

        println("VideoRepository#getSaved(page=$page)")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("page", currentPage.toString())
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#getSaved ${videos.size}")

        return videos.toIntPage(currentPage)
    }

    suspend fun search(query: String, page: Int? = null): IntPage<VideoData> {
        val currentPage = page ?: 1

        if (query.isBlank()) {
            return emptyList<VideoData>().toIntPage(currentPage)
        }

        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/search")
            .build()

        println("VideoRepository#search(query=$query,page=$page)")

        val videos = withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
                url.parameters.append("story", query)
                url.parameters.append("page", currentPage.toString())
            }.validate().body<List<VideoData>>()
        }

        println("VideoRepository#search ${videos.size}")

        return videos.toIntPage(currentPage, pageSize = 100)
    }

    suspend fun toggleFavourite(videoId: Int) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/toggle_fav/$videoId")
            .build()

        println("VideoRepository#toggleFavourite($videoId)")

        withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().discardRemaining()
        }
    }

    suspend fun toggleSaved(videoId: Int) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/toggle_wl/$videoId")
            .build()

        println("VideoRepository#toggleFavourite($videoId)")

        withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().discardRemaining()
        }
    }

    suspend fun addWatched(videoId: Int, details: WatchedVideoData) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/add_watched")
            .buildString()

        println("VideoRepository#setWatched($videoId)")

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
            ) {
                url.parameters.appendAll(getParameters())
            }.validate().discardRemaining()
        }
    }

    suspend fun getVideo(id: Int): VideoDetails {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/post/$id")
            .build()

        println("VideoRepository#getVideo($id)")

        return withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters())
            }.validate().body<VideoDetails>()
        }
    }

    suspend fun getUserProfile(token: String? = null): UserProfile {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/user_profile")
            .build()

        println("VideoRepository#getUserProfile()")

        return withContext(Dispatchers.IO) {
            httpClient.get(requestUrl) {
                url.parameters.appendAll(getParameters(token))
            }.validate().body<UserProfile>()
        }
    }

    suspend fun setVideoServer(server: String) {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("/api/v2/change_server")
            .buildString()

        println("VideoRepository#setVideoServer($server)")

        withContext(Dispatchers.IO) {
            httpClient.submitForm(
                url = requestUrl,
                formParameters = parameters {
                    append("vs_schg", server)
                }
            ) {
                url.parameters.appendAll(getParameters())
            }.validate().discardRemaining()
        }
    }

    private fun List<VideoData>.toIntPage(currentPage: Int, pageSize: Int = 50) = IntPage(
        items = this,
        next = (currentPage + 1).takeIf { size == pageSize }
    )

    private fun getParameters(token: String? = null) = parameters {
        append("user_dev_apk", "2.0.0")
        append("user_dev_id", preferences.deviceId)
        append("user_dev_name", "AndroidTV")
        append("user_dev_os", "12")
        append("user_dev_vendor", "Google")

        val devToken = token ?: preferences.getToken()
        devToken?.let { token ->
            append("user_dev_token", token)
        }
    }
}

private suspend fun HttpResponse.validate(): HttpResponse {
    if (!status.isSuccess()) {
        throw IOException("Failed to execute request, received HTTP ${status.value}: ${bodyAsText()}")
    }
    return this
}
