package org.filmix.app

import kotlinx.io.files.Path
import java.nio.file.Files

class JVMPlatform : Platform {
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
    override val vendorName: String by lazy { System.getProperty("java.vendor") }
    override val deviceName: String by lazy { getComputerName() }
    override val osVersion: String by lazy { System.getProperty("os.version") }

    private fun getComputerName(): String {
        val env = System.getenv()
        return if (env.containsKey("COMPUTERNAME")) {
            env.getValue("COMPUTERNAME")
        } else if (env.containsKey("HOSTNAME")) {
            env.getValue("HOSTNAME")
        } else "Unknown Computer"
    }
}
