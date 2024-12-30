package org.filmix.app.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class Preferences(factory: Settings.Factory) {
    private val settings = factory.create("preferences")
    var theme by mutableStateOf(
        value = settings.get<String>("theme")?.let { name ->
            enumValueOf(name)
        } ?: AppTheme.AUTO
    )
    private set

    var isAuthorized by mutableStateOf(
        value = getToken() != null
    )
        private set

    val deviceId by lazy {
        settings.get<String>("deviceId") ?: run {
            generateDeviceId(16)
        }
    }

    fun getToken(): String? {
        return settings.get<String>("token")
    }

    fun saveDeviceState(deviceId: String, token: String) {
        settings.putString("deviceId", deviceId)
        settings.putString("token", token)
        isAuthorized = true
    }

    fun clearDevicesState() {
        settings.remove("deviceId")
        settings.remove("token")
        isAuthorized = false
    }

    fun updateTheme(appTheme: AppTheme) {
        theme = appTheme
        settings.putString("theme", appTheme.name)
    }

    private fun generateDeviceId(length: Int) : String {
        val allowedChars = "0123456789abcdef".toCharArray()
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}

enum class AppTheme {
    AUTO, DARK, LIGHT
}