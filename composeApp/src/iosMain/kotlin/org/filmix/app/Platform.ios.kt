package org.filmix.app

import kotlinx.io.files.Path
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val isTV: Boolean = false
    override val hasCamera: Boolean = true
    override val downloadDir: Path
        get() = TODO("Not yet implemented")
}
