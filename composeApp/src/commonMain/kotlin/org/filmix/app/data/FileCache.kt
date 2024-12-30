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
    private val cache = Cache.Builder<String, String>()
        .expireAfterWrite(300.seconds)
        .eventListener { event ->
            println("onEvent: $event")
        }
        .build()

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getOrPut(key: String, provider: suspend () -> String): String {
        return cache.get(key) {
            val fileNameHash = key.toByteArray().sha256().hexLower
            val folderPath = Path(cacheDir, fileNameHash.take(2))
            SystemFileSystem.createDirectories(folderPath)
            val filePath = Path(folderPath, fileNameHash)

            try {
                provider().also { data ->
                    SystemFileSystem.sink(filePath).buffered().use { it.writeString(data) }
                }
            } catch (e: Exception) {
                println("FileCache: failed to invalidate cache")
                if (!SystemFileSystem.exists(filePath)) throw e
                SystemFileSystem.source(filePath).buffered().use { it.readString() }
            }
        }
    }

    fun exists(key: String): Boolean {
        return cache.get(key) != null
    }
}