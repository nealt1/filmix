package org.filmix.app.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.DefaultServiceInfo
import org.filmix.app.state.LoadingValue
import org.filmix.app.state.load
import kotlin.time.Duration.Companion.seconds

class SettingsScreenModel(
    private val repository: VideoRepository,
    val preferences: Preferences
) : ScreenModel {
    private var loginAttempt by mutableStateOf(0)
        private set

    private val updateInfoAsync = screenModelScope.async {
        try {
            repository.checkUpdate()
        } catch (e: Throwable) {
            DefaultServiceInfo
        }
    }

    private val loginStateFlow = snapshotFlow { loginAttempt }
        .load {
            val updateInfo = updateInfoAsync.await()
            val token = repository.requestToken().also { request ->
                // Refresh code
                screenModelScope.launch {
                    val tokenExpiration = Instant.fromEpochSeconds(request.expire) - Clock.System.now()
                    println("SettingsScreenModel: waiting for ${tokenExpiration.inWholeSeconds} seconds")
                    delay(tokenExpiration)
                    println("SettingsScreenModel: refreshing pairing code")
                    loginAttempt++
                }

                // Wait for pairing
                screenModelScope.launch {
                    while (true) {
                        delay(10.seconds)

                        try {
                            repository.getUserProfile(token = request.code)
                            break
                        } catch (e: Exception) {
                            println("SettingsScreenModel: pairing not completed")
                            continue
                        }
                    }

                    println("SettingsScreenModel: pairing completed")
                    preferences.saveDeviceState(preferences.deviceId, request.code)
                }
            }

            PairingModel(
                domain = updateInfo.domain,
                code = token.user_code
            )
        }

    val loginState = loginStateFlow
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoadingValue.Loading
        )

    private val userProfileFlow = flowOf(Unit)
        .load {
            try {
                repository.getUserProfile().user_data
            } catch (e: JsonConvertException) {
                println("Failed to get user profile: ${e.message}")
                preferences.clearDevicesState()
                throw e
            }
        }

    val userProfile = userProfileFlow
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoadingValue.Loading
        )

    val themes = enumValues<AppTheme>().associate {
        it to it.name.lowercase().capitalize(Locale.current)
    }

    fun logout() {
        preferences.clearDevicesState()
        loginAttempt++
    }

    fun clearHistory() {
        screenModelScope.launch { repository.clearHistory() }
    }

    fun saveVideoServer(value: String) {
        screenModelScope.launch { repository.setVideoServer(value) }
    }

    class PairingModel(
        val domain: String,
        val code: String
    )
}