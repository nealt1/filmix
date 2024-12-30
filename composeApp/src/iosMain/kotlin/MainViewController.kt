import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.window.ComposeUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import io.kamel.core.config.DefaultHttpCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import org.filmix.app.App
import org.filmix.app.IOSPlatform
import org.filmix.app.di.appModule
import org.filmix.app.di.fileModule
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalWindowSize
import org.filmix.app.ui.WindowSize
import org.koin.compose.KoinApplication
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController {
    val kamelConfig = getKamelConfig()
    val platform = IOSPlatform()
    val settingsFactory = NSUserDefaultsSettings.Factory()
    val windowInfo = LocalWindowInfo.current
    val cacheDirectories = NSSearchPathForDirectoriesInDomains(
        NSCachesDirectory, NSUserDomainMask, true
    )

    KoinApplication(application = {
        modules(
            appModule(settingsFactory),
            fileModule(cacheDirectories.first() as String)
        )
    }) {
        CompositionLocalProvider(
            LocalKamelConfig provides kamelConfig,
            LocalPlatform provides platform,
            LocalWindowSize provides WindowSize(
                width = windowInfo.containerSize.width,
                height = windowInfo.containerSize.height
            )
        ) {
            App()
        }
    }
}

private fun getKamelConfig() = KamelConfig {
    takeFrom(KamelConfig.Default)
    httpFetcher {
        httpCache(DefaultHttpCacheSize)
    }
}
