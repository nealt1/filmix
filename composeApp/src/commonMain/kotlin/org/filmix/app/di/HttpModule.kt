package org.filmix.app.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.filmix.app.data.FileCache
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.lighthousegames.logging.logging

private val log = logging()

internal val httpModule = module {
    fun provideHttpClient(fileCache: FileCache): HttpClient {
        return HttpClient {
            CurlUserAgent()

            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 8_000
                socketTimeoutMillis = 8_000
            }

            install(HttpRequestRetry) {
                retryIf(maxRetries = 5) { request, response ->
                    if (fileCache.exists(request.url.toString())) return@retryIf false
                    response.status.value.let { it == 429 || it in 500..599 }.also { failed ->
                        if (failed) {
                            log.warn { "Failed to execute request ${request.url}: HTTP ${response.status.value}, headers ${response.headers.entries()}" }
                        }
                    }
                }
                exponentialDelay(
                    maxDelayMs = 15_000,
                    respectRetryAfterHeader = false
                )
            }
        }
    }

    singleOf(::provideHttpClient)
}