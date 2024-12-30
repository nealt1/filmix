package org.filmix.app.data

import io.github.reactivecircus.cache4k.Cache
import io.ktor.utils.io.core.toByteArray
import korlibs.crypto.sha256
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.time.Duration.Companion.seconds

class FileCache(private val cacheDir: String) {
    private val cache = Cache.Builder<String, Unit>()
        .expireAfterWrite(300.seconds)
        .eventListener { event ->
            println("onEvent: $event")
        }
        .build()

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getOrPut(url: String, provider: suspend () -> String): String {
        val hash = getUrlHash(url)
        val folderPath = Path(cacheDir, hash.take(2))
        val filePath = Path(folderPath, hash)

        cache.get(hash) {
            SystemFileSystem.createDirectories(folderPath)

            try {
                val data = provider()
                SystemFileSystem.sink(filePath).buffered().use { it.writeString(data) }
            } catch (e: Exception) {
                println("FileCache: failed to invalidate cache")
            }
        }

        return SystemFileSystem.source(filePath).buffered().use { it.readString() }
    }

    fun exists(url: String): Boolean {
        val hash = getUrlHash(url)
        return cache.get(hash) != null
    }

    private fun getUrlHash(url: String) = url.toByteArray().sha256().hexLower
}