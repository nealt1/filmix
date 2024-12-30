package org.filmix.app

import android.app.UiModeManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.DisplayCompat
import com.russhwolf.settings.SharedPreferencesSettings
import io.kamel.core.config.DefaultHttpCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher
import io.kamel.image.config.resourcesIdMapper
import kotlinx.io.files.Path
import org.filmix.app.di.appModule
import org.filmix.app.di.fileModule
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalWindowSize
import org.filmix.app.ui.WindowSize
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val kamelConfig = getKamelConfig()
            val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
            val downloadDir = getExternalFilesDir(DIRECTORY_DOWNLOADS) ?: error("Missing download dir")
            val hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
            val platform = AndroidPlatform(
                uiModeManager = uiModeManager,
                hasCamera = hasCamera,
                downloadDir = Path(downloadDir.path)
            )
            val settingsFactory = SharedPreferencesSettings.Factory(this)
            val display = windowManager.defaultDisplay
            val mode = DisplayCompat.getMode(this, display)
            println("Screen size: ${mode.physicalWidth}x${mode.physicalHeight}")

            KoinApplication(application = {
                modules(
                    appModule(settingsFactory),
                    fileModule(cacheDir.path)
                )
            }) {
                CompositionLocalProvider(
                    LocalKamelConfig provides kamelConfig,
                    LocalPlatform provides platform,
                    LocalWindowSize provides WindowSize(
                        width = mode.physicalWidth,
                        height = mode.physicalHeight
                    )
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
private fun getKamelConfig() = KamelConfig {
    val context = LocalContext.current
    takeFrom(KamelConfig.Default)
    resourcesFetcher(context)
    resourcesIdMapper(context)
    httpFetcher {
        httpCache(DefaultHttpCacheSize)
    }
}