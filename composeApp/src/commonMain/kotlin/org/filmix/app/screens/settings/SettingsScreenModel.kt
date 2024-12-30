package org.filmix.app.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.filmix.app.app.AppState
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.DefaultServiceInfo
import org.filmix.app.state.LoadingValue
import org.filmix.app.state.load
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.seconds

class SettingsScreenModel(
    private val repository: VideoRepository,
    val state: AppState
) : ScreenModel {
    private var loginAttempt by mutableStateOf(0)
        private set

    private val updateInfoAsync by lazy {
        screenModelScope.async {
            try {
                repository.checkUpdate()
            } catch (e: Throwable) {
                DefaultServiceInfo
            }
        }
    }

    private val loginStateFlow = snapshotFlow { loginAttempt }
        .load {
            val updateInfo = updateInfoAsync.await()
            val token = repository.requestToken().also { request ->
                // Refresh code
                screenModelScope.launch {
                    val tokenExpiration = Instant.fromEpochSeconds(request.expire) - Clock.System.now()
                    log.warn { "waiting for refresh code ${tokenExpiration.inWholeSeconds} seconds" }
                    delay(tokenExpiration)
                    log.debug { "refreshing pairing code" }
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
                            log.error(e) { "pairing not completed" }
                            continue
                        }
                    }

                    log.info { "pairing completed" }
                    state.login(request.code)
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

    fun logout() {
        state.logout()
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

    companion object {
        private val log = logging()
    }
}