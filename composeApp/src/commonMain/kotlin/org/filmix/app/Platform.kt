package org.filmix.app

import kotlinx.io.files.Path

interface Platform {
    val name: String
    val isTV: Boolean
    val hasCamera: Boolean
    val hasNetwork: Boolean
    val cacheDir: Path
    val downloadDir: Path
    val vendorName: String
    val deviceName: String
    val osVersion: String
}
