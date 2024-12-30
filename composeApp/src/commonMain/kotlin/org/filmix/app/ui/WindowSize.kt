package org.filmix.app.ui

import androidx.compose.runtime.staticCompositionLocalOf

data class WindowSize(val width: Int, val height: Int)

val LocalWindowSize = staticCompositionLocalOf<WindowSize> {
    error("WindowSize not provided")
}