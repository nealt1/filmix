package org.filmix.app.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.AnonymousUserData
import org.filmix.app.state.LoadingValue
import org.filmix.app.state.load

class AppState(
    private val preferences: Preferences,
    private val repository: VideoRepository,
) {
    val themes = enumValues<AppTheme>().associate {
        it to it.name.lowercase().capitalize(Locale.current)
    }

    var theme by mutableStateOf(
        value = preferences.getTheme()
    )
        private set

    fun updateTheme(theme: AppTheme) {
        this.theme = theme
        preferences.saveTheme(theme)
    }

    var isAuthorized by mutableStateOf(
        value = preferences.getToken() != null
    )
        private set


    private var profileLoad by mutableStateOf(0)
        private set

    private val userProfileFlow = snapshotFlow { profileLoad }.load {
        try {
            repository.getUserProfile().user_data
        } catch (e: JsonConvertException) {
            preferences.clearDevicesState()
            AnonymousUserData
        } catch (e: Throwable) {
            println("Failed to get user profile: ${e.message}")
            AnonymousUserData
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    val userProfile = userProfileFlow
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoadingValue.Loading
        )

    fun login(code: String) {
        preferences.saveDeviceState(code)
        reloadProfile()
    }

    fun logout() {
        preferences.clearDevicesState()
        reloadProfile()
    }

    private fun reloadProfile() {
        profileLoad++
    }
}