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
import org.filmix.app.composeapp.generated.resources.*
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
import org.jetbrains.compose.resources.stringResource

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
            item { SectionTitle(text = stringResource(Res.string.settings_authorization_title)) }

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
                                    append(stringResource(Res.string.settings_pro_active))
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
                        title = stringResource(Res.string.settings_server),
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
                        title = stringResource(Res.string.settings_history),
                        subtitle = stringResource(Res.string.settings_history_description),
                        onClick = { model.clearHistory() }
                    )
                }

                item {
                    RegularPreference(
                        title = stringResource(Res.string.settings_logout),
                        subtitle = stringResource(Res.string.settings_logout_description),
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
                                Text(stringResource(Res.string.action_cancel))
                            }

                            QrScannerScreen(Modifier.size(320.dp)) { data ->
                                model.state.loadState(data)
                            }
                        }
                    } else {
                        RegularPreference(
                            title = stringResource(Res.string.settings_login),
                            subtitle = stringResource(Res.string.settings_login_description),
                            onClick = { showLogin = true }
                        )
                    }

                    if (platformClicks >= 5) {
                        RegularPreference(
                            title = stringResource(Res.string.settings_scan_code),
                            subtitle = stringResource(Res.string.settings_scan_code_description),
                            onClick = { showScanner = true }
                        )
                    }
                }
            }

            item { SectionTitle(text = stringResource(Res.string.settings_interface_title)) }

            item {
                DropdownPreference(
                    title = stringResource(Res.string.settings_theme),
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
                    title = stringResource(Res.string.settings_platform),
                    subtitle = "${platform.name}$tvSuffix",
                    enabled = platform.hasCamera && !user.isAuthorized,
                    onClick = { platformClicks++ }
                )
            }

            if (profileClicks >= 5) {
                item {
                    val stateData = model.state.shareState()

                    SectionTitle(
                        text = stringResource(Res.string.settings_new_device)
                    )

                    TextPreference(
                        title = stringResource(Res.string.settings_scan_code),
                        subtitle = stringResource(Res.string.settings_new_device_description)
                    )

                    ShowImage(
                        painter = rememberQrCodePainter(stateData),
                        description = stringResource(Res.string.settings_scan_code_url),
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
                    Text(stringResource(Res.string.settings_code_wait))
                }
            },
            failed = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShowError(it)

                    Button(onClick = { model.logout() }) {
                        Text(stringResource(Res.string.action_repeat))
                    }
                }
            }
        ) {
            val uriHandler = LocalUriHandler.current
            val consolesUrl = "$domain/consoles"

            RegularPreference(
                title = stringResource(Res.string.settings_open_url),
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
                title = stringResource(Res.string.settings_device_id),
                subtitle = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(code)
                    }
                }
            )

            ShowImage(
                painter = rememberQrCodePainter(consolesUrl),
                description = stringResource(Res.string.settings_scan_code_url)
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
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}