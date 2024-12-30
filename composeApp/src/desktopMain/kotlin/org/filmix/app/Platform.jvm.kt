package org.filmix.app

import kotlinx.io.files.Path

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val isTV: Boolean = false
    override val hasCamera: Boolean = false
    override val downloadDir: Path by lazy {
        val home = System.getProperty("user.home")
        Path(home, "Downloads")
    }
}
