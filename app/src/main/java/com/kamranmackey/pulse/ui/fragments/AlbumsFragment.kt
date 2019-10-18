package com.kamranmackey.pulse.ui.fragments

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.adapters.AlbumAdapter
import com.kamranmackey.pulse.backend.models.Album
import com.kamranmackey.pulse.utils.extensions.baseActivity
import java.util.ArrayList

class AlbumsFragment : Fragment() {

    private val albums = ArrayList<Album>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: AlbumAdapter
    private lateinit var mContext: Context

    companion object {
        fun newInstance() = AlbumsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.albums_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.albumRecyclerView)
        mAdapter = AlbumAdapter(albums)
        mContext = baseActivity

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mContext)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = mAdapter

        getAlbumData()
    }

    private fun getAlbumData() {
        val resolver: ContentResolver = baseActivity.contentResolver
        val albumUri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(albumUri, null, null, null, null)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id: String = cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val albumId: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID))
                val album: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
                val tracks: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS))
                albums.add(Album(id, albumId, album, artist, tracks))
            }
            cursor.close()
            mAdapter.notifyDataSetChanged()
        }
    }

}
