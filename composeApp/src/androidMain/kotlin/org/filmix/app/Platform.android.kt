package org.filmix.app

import android.app.UiModeManager
import android.content.res.Configuration
import android.os.Build
import kotlinx.io.files.Path

class AndroidPlatform(
    private val uiModeManager: UiModeManager,
    override val hasCamera: Boolean,
    override val cacheDir: Path,
    override val downloadDir: Path,
    private val getHasNetwork: () -> Boolean
) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val isTV: Boolean
        get() = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION

    override val hasNetwork: Boolean
        get() = getHasNetwork()
}