package org.filmix.app.components

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.HttpRequestCreated
import io.ktor.client.utils.unwrapCancellationException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import io.ktor.util.toMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.logging
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

@UnstableApi
class KtorHttpDataSource private constructor(
    private val httpClient: HttpClient,
    private val requestOptions: MutableMap<String, String>,
) : BaseDataSource(true), HttpDataSource {
    class Factory(private val httpClient: HttpClient) :
        HttpDataSource.Factory {
        private val requestOptions = mutableMapOf<String, String>()

        override fun createDataSource(): HttpDataSource {
            return KtorHttpDataSource(httpClient, requestOptions)
        }

        override fun setDefaultRequestProperties(defaultRequestProperties: MutableMap<String, String>): HttpDataSource.Factory {
            requestOptions.putAll(defaultRequestProperties)
            return this
        }
    }

    private lateinit var request: DataRequest
    private lateinit var response: DataResponse
    private var bytesRemaining: Long = 0
    private var opened = false

    override fun open(dataSpec: DataSpec): Long {
        transferInitializing(dataSpec)

        request = DataRequest(
            uri = dataSpec.uri,
            position = dataSpec.position,
            length = dataSpec.length
        )

        response = try {
            runBlocking(Dispatchers.IO) {
                openRequest(request)
            }
        } catch (e: InterruptedException) {
            return 0
        } catch (e: Exception) {
            log.warn(e) { "failed to open ${dataSpec.uri}" }
            return 0
        }

        log.debug { "open(${dataSpec.uri}, ${dataSpec.position}/${dataSpec.length}): length ${response.contentLength}" }

        opened = true
        bytesRemaining = dataSpec.length - dataSpec.position

        transferStarted(dataSpec)

        return response.contentLength
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) {
            return 0
        } else if (bytesRemaining == 0L) {
            return C.RESULT_END_OF_INPUT
        }

        var bytesRead: Int = -1

        do {
            try {
                bytesRead = response.stream.read(buffer, offset, length)
            } catch (e: HttpRequestTimeoutException) {
                log.warn(e) { "read(size: ${buffer.size}, offset=${offset}, length=${length}): failed to read bytes ${e.message}" }
                response = runBlocking(Dispatchers.IO) {
                    openRequest(
                        request.copy(
                            position = response.contentLength - bytesRead
                        )
                    )
                }
            }
        } while (bytesRead < 0)

        bytesRemaining -= bytesRead

        bytesTransferred(bytesRead)

        return bytesRead
    }

    override fun close() {
        if (!opened) return
        opened = false

        log.debug { "close()" }
        response.stream.close()
        transferEnded()
    }

    override fun setRequestProperty(name: String, value: String) {
        requestOptions[name] = value
    }

    override fun clearRequestProperty(name: String) {
        requestOptions.remove(name)
    }

    override fun clearAllRequestProperties() {
        requestOptions.clear()
    }

    override fun getUri(): Uri {
        return request.uri
    }

    override fun getResponseCode(): Int {
        return response.statusCode
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        return response.headers
    }

    private suspend fun openRequest(request: DataRequest): DataResponse {
        log.debug { "openRequest(): $request" }
        val requestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url.takeFrom(request.uri.toString())
            headers {
                val rangeHeader = HttpUtil.buildRangeRequestHeader(
                    request.position,
                    request.length
                )
                rangeHeader?.let { value ->
                    append(HttpHeaders.Range, value)
                }
            }
            timeout {
                requestTimeoutMillis = 30_000
            }
        }
        val response = executeUnsafe(
            requestBuilder
        )

        log.debug { "openRequest(): response ${response.status}" }
        val contentLength = response.headers[HttpHeaders.ContentLength]?.toLong() ?: 0

        return DataResponse(
            statusCode = response.status.value,
            contentLength = contentLength,
            headers = response.headers.toMap(),
            stream = response.body()
        )
    }

    private data class DataRequest(
        val uri: Uri,
        val position: Long,
        val length: Long
    )

    private class DataResponse(
        val statusCode: Int,
        val contentLength: Long,
        val headers: Map<String, List<String>>,
        val stream: InputStream
    )

    private suspend fun executeUnsafe(builder: HttpRequestBuilder): HttpResponse = unwrapRequestTimeoutException {
        val call = httpClient.execute(builder)
        return call.response
    }

    private inline fun <T> unwrapRequestTimeoutException(block: () -> T): T {
        try {
            return block()
        } catch (cause: CancellationException) {
            throw cause.unwrapCancellationException()
        }
    }

    private suspend fun HttpClient.execute(builder: HttpRequestBuilder): HttpClientCall {
        monitor.raise(HttpRequestCreated, builder)
        return requestPipeline.execute(builder, builder.body) as HttpClientCall
    }

    companion object {
        private val log = logging()
    }
}