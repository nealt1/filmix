package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenRequest(
    val status: String,
    val code: String,
    val user_code: String,
    val expire: Long
)