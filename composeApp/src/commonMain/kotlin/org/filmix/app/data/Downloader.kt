package org.filmix.app.data

import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class Downloader(private val httpClient: HttpClient) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun downloadFile(
        url: String,
        filePath: Path,
        onState: suspend (DownloadState) -> Unit
    ) {
        println("Download $url to $filePath")

        onState(DownloadState.Downloading(0.0f))

        withContext(Dispatchers.IO) {
            httpClient.prepareGet(url).execute { response ->
                val contentLength = response.contentLength() ?: error("Unknown content length")
                val channel = response.bodyAsChannel()

                SystemFileSystem.sink(filePath).buffered().use { file ->
                    val byteArray = ByteArray(DEFAULT_BUFFER_SIZE)
                    do {
                        val readBytes = channel.readAvailable(byteArray, 0, byteArray.size)
                        if (readBytes > 0) file.write(byteArray, 0, readBytes)
                        onState(DownloadState.Downloading(channel.totalBytesRead.toFloat() / contentLength))
                    } while (channel.totalBytesRead < contentLength)

                    file.flush()
                }

                onState(DownloadState.Success)
            }
        }
    }

    companion object {
        private val DEFAULT_BUFFER_SIZE = 8196
    }
}

sealed class DownloadState {
    data object None : DownloadState()
    data object Success : DownloadState()
    class Downloading(val progress: Float) : DownloadState()
}