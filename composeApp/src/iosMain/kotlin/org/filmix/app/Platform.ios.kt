package org.filmix.app

import kotlinx.io.files.Path
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String by lazy {
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    }
    override val isTV: Boolean = false
    override val hasCamera: Boolean = true
    override val hasNetwork: Boolean = true
    override val cacheDir: Path by lazy {
        val cacheDirectories = NSSearchPathForDirectoriesInDomains(
            NSCachesDirectory, NSUserDomainMask, true
        )
        Path(cacheDirectories.first() as String)
    }
    override val downloadDir: Path
        get() = TODO("Not yet implemented")
}
