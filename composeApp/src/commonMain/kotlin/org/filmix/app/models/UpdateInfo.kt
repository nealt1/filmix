package org.filmix.app.models

import kotlinx.serialization.Serializable

interface ServiceInfo {
    val domain: String
}

@Serializable
data class UpdateInfo(
    val version: String,
    val release_date: String,
    val features: List<String>,
    val update_message: String,
    val update_url: String,
    val ads_url: String,
    val pro_url: String,
    override val domain: String
) : ServiceInfo

object DefaultServiceInfo: ServiceInfo {
    override val domain: String = "https://filmix.ac"
}