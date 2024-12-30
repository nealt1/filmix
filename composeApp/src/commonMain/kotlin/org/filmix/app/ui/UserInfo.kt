package org.filmix.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import org.filmix.app.models.UserInfo

val LocalUserInfo = staticCompositionLocalOf<UserInfo> {
    error("UserInfo not provided")
}

@Composable
fun ProvideUserInfo(
    userInfo: UserInfo,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalUserInfo provides userInfo, content = content)
}