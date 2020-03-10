package com.kamranmackey.pulse.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.*


object MusicUtils {

    fun getAlbumArtFromMediaStore(mediaId: Long, context: Context): Bitmap {
        val uri: Uri = Uri.parse("content://media/external/audio/albumart")
        val contentUri = ContentUris.withAppendedId(uri, mediaId);
        return MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
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

    @Suppress("DEPRECATION")
    fun getBitmapUri(resolver: ContentResolver, uri: Uri): Bitmap? {
        val bitmap: Bitmap?

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            return try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            return try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))
                bitmap
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    fun deleteSongFromDevice(
        position: Int,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {

    }

}