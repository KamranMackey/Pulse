package com.kamranmackey.pulse.backend.models

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val track: Int,
    val album: String,
    val year: Int,
    val path: String
)