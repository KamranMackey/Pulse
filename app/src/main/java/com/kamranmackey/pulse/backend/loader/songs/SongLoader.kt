package com.kamranmackey.pulse.backend.loader.songs

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import com.kamranmackey.pulse.backend.model.Song


class SongLoader {

    companion object {
        fun getAllSongs(context: Context): List<Song> {
            val cursor = createSongCursor(context)
            val songs: MutableList<Song> = ArrayList()
            if (cursor.moveToFirst()) {
                do {
                    val id: Int = cursor.getInt(0)
                    val name: String = cursor.getString(1)
                    val artist: String = cursor.getString(2)
                    val album: String = cursor.getString(3)
                    val duration: Long = cursor.getLong(4)
                    val trackNumber: Int = cursor.getInt(5)
                    val artistId: Long = cursor.getLong(6)
                    val year: Int = cursor.getInt(7)
                    val albumId: Long = cursor.getLong(8)
                    val path: String = cursor.getString(9);
                    songs.add(Song(id, albumId, artistId, name, artist, album, duration, year, trackNumber, path))
                } while (cursor.moveToNext())
            }

            cursor.close()

            return songs
        }

        private fun createSongCursor(context: Context): Cursor {
            return createSongCursor(context, MediaStore.Audio.AudioColumns.IS_MUSIC + "=1")
        }

        @SuppressLint("InlinedApi", "Recycle")
        fun createSongCursor(context: Context, selection: String?): Cursor {
            val sortOrder: String = MediaStore.Audio.Media.DEFAULT_SORT_ORDER + " ASC"

            return context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
                    BaseColumns._ID,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    MediaStore.Audio.AudioColumns.DURATION,
                    MediaStore.Audio.AudioColumns.TRACK,
                    MediaStore.Audio.AudioColumns.YEAR,
                    MediaStore.Audio.AudioColumns.ARTIST_ID,
                    MediaStore.Audio.AudioColumns.ALBUM_ID,
                    MediaStore.Audio.AudioColumns.DATA
                ), selection,
                null,
                sortOrder
            )!!
        }
    }
}