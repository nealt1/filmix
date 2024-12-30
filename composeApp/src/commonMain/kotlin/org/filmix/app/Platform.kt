package org.filmix.app

import kotlinx.io.files.Path

interface Platform {
    val name: String
    val isTV: Boolean
    val downloadDir: Path
}
