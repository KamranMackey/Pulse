package com.kamranmackey.pulse.backend.loader.albums

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import com.kamranmackey.pulse.backend.model.Album

class AlbumLoader {

    companion object {
        fun getAllAlbums(context: Context): List<Album> {
            val cursor = createAlbumCursor(context)
            val albums: MutableList<Album> = ArrayList()
            if (cursor.moveToFirst()) {
                do {
                    val id: Int = cursor.getInt(0)
                    val name: String = cursor.getString(1)
                    val artist: String = cursor.getString(2)
                    val artistId: Int = cursor.getInt(3)
                    val tracks: Int = cursor.getInt(4)
                    val year: Int = cursor.getInt(5)

                    val album = Album(id, artistId, name, artist, tracks, year)
                    albums.add(album)

                } while (cursor.moveToNext())
            }

            cursor.close()

            return albums
        }

        @SuppressLint("InlinedApi", "Recycle")
        fun createAlbumCursor(context: Context): Cursor {
            val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            val order: String = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER + " ASC"

            val columns = arrayOf(
                BaseColumns._ID,
                MediaStore.Audio.AlbumColumns.ALBUM,
                MediaStore.Audio.AlbumColumns.ARTIST,
                MediaStore.Audio.AlbumColumns.ARTIST_ID,
                MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
                MediaStore.Audio.AlbumColumns.FIRST_YEAR
            )

            return context.contentResolver.query(uri, columns, null, null, order)!!
        }
    }
}