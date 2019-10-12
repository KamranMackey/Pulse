package com.kamranmackey.pulse.backend.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val track: Int,
    val album: String,
    val albumArtist: String,
    val year: Int,
    val path: String
) : Parcelable