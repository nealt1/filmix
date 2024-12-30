import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.russhwolf.settings.PreferencesSettings
import io.kamel.core.config.DefaultHttpCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.batikSvgDecoder
import io.kamel.image.config.resourcesFetcher
import org.filmix.app.App
import org.filmix.app.JVMPlatform
import org.filmix.app.di.appModule
import org.filmix.app.di.fileModule
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalWindowSize
import org.filmix.app.ui.WindowSize
import org.koin.compose.KoinApplication
import java.nio.file.Files

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val kamelConfig = getKamelConfig()
    val platform = JVMPlatform()
    val settingsFactory = PreferencesSettings.Factory()
    val cacheDir = Files.createTempDirectory("filmix")

    Window(onCloseRequest = ::exitApplication, title = "filmix") {
        KoinApplication(application = {
            modules(
                appModule(settingsFactory),
                fileModule(cacheDir.toString())
            )
        }) {
            val windowInfo = LocalWindowInfo.current
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
}

private fun getKamelConfig() = KamelConfig {
    takeFrom(KamelConfig.Default)
    resourcesFetcher()
    batikSvgDecoder()
    httpFetcher {
        httpCache(DefaultHttpCacheSize)
    }
}