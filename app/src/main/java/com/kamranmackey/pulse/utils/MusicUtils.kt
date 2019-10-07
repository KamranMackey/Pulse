package com.kamranmackey.pulse.utils

import android.content.ContentUris
import android.net.Uri
import java.util.*

object MusicUtils {

    fun getAlbumArtFromMediaStore(albumId: Long): Uri {
        val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(artworkUri, albumId)
    }

    fun getSongDuration(songDurationMillis: Int): String {
        var minutes = songDurationMillis / 1000 / 60
        val seconds = songDurationMillis / 1000 % 60
        return if (minutes < 60) {
            String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds)
        } else {
            val hours = minutes / 60
            minutes %= 60
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        }
    }

}