package com.kamranmackey.pulse.backend.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(val id: Int,
                 val albumId: Int,
                 val title: String,
                 val artist: String,
                 val tracks: Int): Parcelable