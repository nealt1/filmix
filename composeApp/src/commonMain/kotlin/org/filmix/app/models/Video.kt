package org.filmix.app.models

interface Video {
    val id: Int
    val title: String
    val poster: String
    val year: Int
    val alt_name: String
}