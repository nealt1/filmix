package org.filmix.app.ui

import androidx.compose.runtime.staticCompositionLocalOf
import org.filmix.app.Platform

val LocalPlatform = staticCompositionLocalOf<Platform> {
    error("Platform not provided")
}
