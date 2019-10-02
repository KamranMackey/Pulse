package com.kamranmackey.pulse.backend.models

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val year: Int
)