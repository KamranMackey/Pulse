package com.kamranmackey.pulse.backend.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val duration: Long,
    val year: Int,
    val trackNumber: Int,
    val path: String
) : Parcelable