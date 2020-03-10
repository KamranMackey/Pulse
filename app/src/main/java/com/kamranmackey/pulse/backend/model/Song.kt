package com.kamranmackey.pulse.backend.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: Int,
    val albumId: Long,
    val artistId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val year: Int,
    val trackNumber: Int,
    val path: String
) : Parcelable