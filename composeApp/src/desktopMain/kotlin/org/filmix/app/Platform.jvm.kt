package org.filmix.app

import kotlinx.io.files.Path
import java.nio.file.Files

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val isTV: Boolean = false
    override val hasCamera: Boolean = false
    override val hasNetwork: Boolean = true
    override val cacheDir: Path by lazy {
        val cacheDir = Files.createTempDirectory("filmix")
        Path(cacheDir.toString())
    }
    override val downloadDir: Path by lazy {
        val home = System.getProperty("user.home")
        Path(home, "Downloads")
    }
}
