package org.filmix.app.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
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
import org.filmix.app.components.QrScannerScreen
import org.filmix.app.components.SectionTitle
import org.filmix.app.components.ShowError
import org.filmix.app.models.UserData
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalUserInfo
import org.filmix.app.ui.preference.DropdownPreference
import org.filmix.app.ui.preference.RegularPreference
import org.filmix.app.ui.preference.TextPreference

object SettingsScreen : Screen {

    override val key = "SettingsScreen"

    @Composable
    override fun Content() {
        val platform = LocalPlatform.current
        val user = LocalUserInfo.current
        val model = getScreenModel<SettingsScreenModel>()

        val state = remember { model.state }
        var platformClicks by remember { mutableStateOf(0) }
        var profileClicks by remember { mutableStateOf(0) }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { SectionTitle(text = "Authorization") }

            if (user.isAuthorized) {
                val userData = user as UserData
                val isPro = userData.is_pro || userData.is_pro_plus

                item {
                    TextPreference(
                        title = userData.login,
                        subtitle = userData.display_name
                    )
                }

                if (isPro) {
                    item {
                        RegularPreference(
                            title = if (userData.is_pro_plus) {
                                "PRO+"
                            } else if (userData.is_pro) {
                                "PRO"
                            } else "",
                            subtitle = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    append("active")
                                }

                                append(" (${userData.pro_date})")
                            },
                            onClick = { profileClicks++ }
                        )
                    }
                }

                item {
                    val videoServer = remember {
                        mutableStateOf(
                            value = userData.videoserver.ifEmpty { "AUTO" }
                        )
                    }

                    DropdownPreference(
                        title = "Server",
                        items = userData.available_servers,
                        selectedItem = videoServer.value,
                        onItemSelected = { server ->
                            videoServer.value = server
                            model.saveVideoServer(server)
                        },
                    )
                }

                item {
                    RegularPreference(
                        title = "Watch history",
                        subtitle = "Click to clear your watch history",
                        onClick = { model.clearHistory() }
                    )
                }

                item {
                    RegularPreference(
                        title = "Logout",
                        subtitle = "Click to end your current session",
                        onClick = { model.logout() }
                    )
                }
            } else {
                item {
                    var showLogin by remember { mutableStateOf(false) }
                    var showScanner by remember { mutableStateOf(false) }

                    if (showLogin) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ShowLogin(model)
                        }
                    } else if (showScanner) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(onClick = { showScanner = false }) {
                                Text("Cancel")
                            }

                            QrScannerScreen(Modifier.size(320.dp)) { data ->
                                model.state.loadState(data)
                            }
                        }
                    } else {
                        RegularPreference(
                            title = "Login",
                            subtitle = "Click to authenticate the device",
                            onClick = { showLogin = true }
                        )
                    }

                    if (platformClicks >= 5) {
                        RegularPreference(
                            title = "Scan code",
                            subtitle = "Click to scan code from other device",
                            onClick = { showScanner = true }
                        )
                    }
                }
            }

            item { SectionTitle(text = "Interface") }

            item {
                DropdownPreference(
                    title = "Theme",
                    items = state.themes,
                    selectedItem = state.theme,
                    onItemSelected = { theme ->
                        state.updateTheme(theme)
                    }
                )
            }

            item {
                val tvSuffix = if (platform.isTV) "/TV" else ""
                RegularPreference(
                    title = "Platform",
                    subtitle = "${platform.name}$tvSuffix",
                    enabled = platform.hasCamera && !user.isAuthorized,
                    onClick = { platformClicks++ }
                )
            }

            if (profileClicks >= 5) {
                item {
                    val stateData = model.state.shareState()

                    SectionTitle(
                        text = "Authorize new device"
                    )

                    TextPreference(
                        title = "Scan code",
                        subtitle = "Open the app on your new device and scan the code"
                    )

                    ShowImage(
                        painter = rememberQrCodePainter(stateData),
                        description = "Scan QR code to open the page",
                        modifier = Modifier.clickable(
                            onClick = { profileClicks = 0 }
                        ).padding(bottom = 16.dp)
                    )
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
            val uriHandler = LocalUriHandler.current
            val consolesUrl = "$domain/consoles"

            RegularPreference(
                title = "Open URL",
                subtitle = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(consolesUrl)
                    }
                },
                onClick = { uriHandler.openUri(consolesUrl) }
            )

            TextPreference(
                title = "Device ID",
                subtitle = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(code)
                    }
                }
            )

            ShowImage(
                painter = rememberQrCodePainter(consolesUrl),
                description = "Scan QR code to open the page"
            )
        }
    }

    @Composable
    private fun ShowImage(
        painter: Painter,
        description: String,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = description,
                modifier = Modifier.size(320.dp)
                    .background(Color.White)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}