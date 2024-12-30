package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val user_data: UserData
)

interface UserInfo {
    val isAuthorized: Boolean
    val is_pro: Boolean
    val is_pro_plus: Boolean
}

@Serializable
data class UserData(
    val login: String,
    val foto: String,
    override val is_pro: Boolean,
    override val is_pro_plus: Boolean,
    val pro_date: String?,
    val display_name: String,
    val videoserver: String,
    val available_servers: Map<String, String>
) : UserInfo {
    override val isAuthorized = true
}

object AnonymousUserData : UserInfo {
    override val isAuthorized = false
    override val is_pro = false
    override val is_pro_plus = false
}