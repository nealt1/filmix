package org.filmix.app.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.filmix.app.components.LoadingIndicator
import org.filmix.app.components.ShowError
import org.filmix.app.ui.LocalPlatform

object SettingsScreen : Screen {

    override val key = "SettingsScreen"

    @Composable
    override fun Content() {
        val platform = LocalPlatform.current
        val model = getScreenModel<SettingsScreenModel>()

        val preferences = remember { model.preferences }
        var showThemes by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(8.dp)) {
            val tvSuffix = if (platform.isTV) "/TV" else ""
            Text("Platform: ${platform.name}$tvSuffix")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Theme")

                Button(onClick = { showThemes = true }) {
                    Text(model.themes[preferences.theme].orEmpty())

                    DropdownMenu(
                        expanded = showThemes,
                        onDismissRequest = { showThemes = false }
                    ) {
                        model.themes.forEach { (theme, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    preferences.updateTheme(theme)
                                    showThemes = false
                                },
                                enabled = theme != preferences.theme
                            )
                        }
                    }
                }
            }

            if (preferences.isAuthorized) {
                ShowUserDetails(model)
            } else {
                var showLogin by remember { mutableStateOf(false) }
                if (showLogin) {
                    ShowLogin(model)
                } else {
                    Button(onClick = { showLogin = true }) {
                        Text("Login")
                    }
                }
            }
        }
    }

    @Composable
    private fun ShowUserDetails(model: SettingsScreenModel) {
        val userProfile by model.userProfile.collectAsState()
        var showServers by remember { mutableStateOf(false) }

        LoadingIndicator(userProfile) {
            val isPro = is_pro || is_pro_plus
            val videoServer = remember {
                mutableStateOf(
                    value = videoserver.ifEmpty { "AUTO" }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("$display_name ($login)")

                Button(onClick = { model.logout() }) {
                    Text("Logout")
                }
            }

            if (isPro) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(buildString {
                        if (is_pro_plus) {
                            append("PRO+")
                        } else if (is_pro) {
                            append("PRO")
                        }
                        append(": till $pro_date")
                    })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Server")

                    Button(onClick = { showServers = true }) {
                        Text(available_servers[videoServer.value].orEmpty())

                        DropdownMenu(
                            expanded = showServers,
                            onDismissRequest = { showServers = false }
                        ) {
                            available_servers.forEach { (value, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        videoServer.value = value
                                        model.saveVideoServer(value)
                                        showServers = false
                                    },
                                    enabled = value != videoServer.value
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Watch history")

                Button(onClick = { model.clearHistory() }) {
                    Text("Clean")
                }
            }
        }
    }

    @Composable
    private fun ShowLogin(model: SettingsScreenModel) {
        val loginState by model.loginState.collectAsState()

        LoadingIndicator(loginState,
            loading = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Text("It may take few minutes")
                }
            },
            failed = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShowError(it)

                    Button(onClick = { model.logout() }) {
                        Text("Try again")
                    }
                }
            }
        ) {
            val consolesUrl = "$domain/consoles"

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        append("Open ")

                        withStyle(style = SpanStyle(
                            color = MaterialTheme.colors.primaryVariant,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append(consolesUrl)
                        }

                        append(" in browser and enter the following code: ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(code)
                        }
                    }
                )

                Image(
                    painter = rememberQrCodePainter(consolesUrl),
                    contentDescription = "Scan QR code to open the page",
                    modifier = Modifier.size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}