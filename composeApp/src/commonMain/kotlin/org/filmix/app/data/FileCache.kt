package org.filmix.app.data

import io.github.reactivecircus.cache4k.Cache
import io.ktor.utils.io.core.toByteArray
import korlibs.crypto.sha256
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import org.filmix.app.Platform
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.seconds

class FileCache(private val platform: Platform) {
    init {
        log.info { "Using cache ${platform.cacheDir}" }
    }

    private val cache = Cache.Builder<String, Unit>()
        .expireAfterWrite(300.seconds)
        .eventListener { event ->
            log.debug { "Cache event $event" }
        }
        .build()

    suspend fun getOrPut(url: String, provider: suspend () -> String): String {
        val hash = getUrlHash(url)
        val folderPath = Path(platform.cacheDir, hash.take(2))
        val filePath = Path(folderPath, hash)

        if (platform.hasNetwork) {
            cache.get(hash) {
                SystemFileSystem.createDirectories(folderPath)

                try {
                    val data = provider()
                    SystemFileSystem.sink(filePath).buffered().use { it.writeString(data) }
                } catch (e: Exception) {
                    log.error(e) { "Failed to invalidate cache for $url" }
                }
            }
        }

        return SystemFileSystem.source(filePath).buffered().use { it.readString() }
    }

    fun exists(url: String): Boolean {
        val hash = getUrlHash(url)
        return cache.get(hash) != null
    }

    private fun getUrlHash(url: String) = url.toByteArray().sha256().hexLower

    companion object {
        private val log = logging()
    }
}