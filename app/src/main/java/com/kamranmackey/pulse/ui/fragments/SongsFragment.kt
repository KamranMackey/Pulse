package com.kamranmackey.pulse.ui.fragments

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.BaseColumns
import android.provider.MediaStore
import android.view.*
import androidx.core.app.ActivityCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.adapters.SongAdapter
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.ui.dialogs.AboutDialog
import com.kamranmackey.pulse.utils.extensions.baseActivity

import java.util.ArrayList


class SongsFragment : Fragment() {

    private val songList = ArrayList<Song>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: SongAdapter
    private lateinit var mContext: Context
    private lateinit var mManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mManager = fragmentManager!!

        recyclerView = view.findViewById(R.id.recyclerView)
        mAdapter = SongAdapter(songList, mManager)
        mContext = baseActivity

        val layoutManager: LayoutManager = LinearLayoutManager(mContext)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = mAdapter

        getSongData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                AboutDialog.show(baseActivity)
            }
        }
        return true
    }

    private fun getSongData() {
        val resolver: ContentResolver = baseActivity.contentResolver
        val song: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection: String = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1")

        val cursor: Cursor? = resolver.query(song, null, selection, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndexOrThrow(BaseColumns._ID)
            val title: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artist: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val album: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumArtist: Int = cursor.getColumnIndexOrThrow("album_artist")
            val track: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val year: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val path: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            do {
                val currentId: Long = cursor.getLong(id)
                val currentTitle: String = cursor.getString(title)
                val currentArtist: String = cursor.getString(artist)
                val currentAlbum: String = cursor.getString(album)
                val currentAlbumArtist: String = cursor.getString(albumArtist)
                val currentTrack: Int = cursor.getInt(track)
                val currentYear: Int = cursor.getInt(year)
                val currentPath: String = cursor.getString(path)
                songList.add(
                    Song(
                        currentId, currentTitle, currentArtist, currentTrack,
                        currentAlbum, currentAlbumArtist, currentYear, currentPath
                    )
                )
            } while (cursor.moveToNext())

            cursor.close()
            mAdapter.notifyDataSetChanged()
        }
    }
}
