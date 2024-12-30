package org.filmix.app.app

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class Preferences(factory: Settings.Factory) {
    private val settings = factory.create("preferences")

    fun getTheme(): AppTheme {
        return settings.get<String>("theme")?.let { name ->
            enumValueOf<AppTheme>(name)
        } ?: AppTheme.AUTO
    }

    val deviceId by lazy {
        settings.get<String>("deviceId") ?: run {
            generateDeviceId(16)
        }
    }

    fun getToken(): String? {
        return settings.get<String>("token")
    }

    fun saveDeviceState(token: String) {
        settings.putString("deviceId", deviceId)
        settings.putString("token", token)
    }

    fun clearDevicesState() {
        settings.remove("deviceId")
        settings.remove("token")
    }

    fun saveTheme(appTheme: AppTheme) {
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