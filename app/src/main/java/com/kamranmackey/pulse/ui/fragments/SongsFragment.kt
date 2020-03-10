package com.kamranmackey.pulse.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.adapters.SongAdapter
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.utils.extensions.baseActivity
import java.util.*


class SongsFragment : Fragment() {

    private val songs = ArrayList<Song>()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: SongAdapter
    private lateinit var mContext: Context
    private lateinit var mManager: FragmentManager

    companion object {
        fun newInstance() = SongsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mManager = fragmentManager as FragmentManager

        mRecyclerView = view.findViewById(R.id.songRecyclerView)
        mAdapter = SongAdapter(songs, mManager)
        mContext = baseActivity

        val layoutManager: LayoutManager = LinearLayoutManager(mContext)

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.adapter = mAdapter

        getSongData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("InlinedApi")
    private fun getSongData() {
        val resolver: ContentResolver = mContext.contentResolver
        val song: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? = resolver.query(song, null, selection, null, sortOrder)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id: Long = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val albumId: Long = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val title: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val duration: Long = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val album: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val albumArtist: String = cursor.getString(cursor.getColumnIndexOrThrow("album_artist"))
                val track: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                val year: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
                val path: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                songs.add(Song(id, albumId, title, artist, album, albumArtist, duration, track, year, path))
            }
            cursor.close()
            mAdapter.notifyDataSetChanged()
        }
    }
}
