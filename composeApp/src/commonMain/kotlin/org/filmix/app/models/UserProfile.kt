package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val user_data: UserData
)

@Serializable
data class UserData(
    val login: String,
    val foto: String,
    val is_pro: Boolean,
    val is_pro_plus: Boolean,
    val pro_date: String?,
    val display_name: String,
    val videoserver: String,
    val available_servers: Map<String, String>
)

@Serializable
data class ChangeServer(
    val vs_schg: String
)