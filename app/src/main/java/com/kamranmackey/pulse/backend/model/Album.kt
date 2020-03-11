package com.kamranmackey.pulse.backend.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    val id: Int,
    val artistId: Int,
    val title: String,
    val artist: String,
    val tracks: Int,
    val year: Int
) : Parcelable