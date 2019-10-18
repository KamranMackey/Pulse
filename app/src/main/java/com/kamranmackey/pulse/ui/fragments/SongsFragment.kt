package com.kamranmackey.pulse.ui.fragments

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.*
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
import com.kamranmackey.pulse.backend.services.PlayerService
import com.kamranmackey.pulse.utils.extensions.baseActivity

import java.util.ArrayList
import android.widget.Toast
import android.os.IBinder

class SongsFragment : Fragment() {

    private val songList = ArrayList<Song>()

    private lateinit var player: PlayerService

    var serviceBound = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: SongAdapter
    private lateinit var mContext: Context
    private lateinit var mManager: FragmentManager

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

        recyclerView = view.findViewById(R.id.songRecyclerView)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("serviceStatus", serviceBound)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            serviceBound = savedInstanceState.getBoolean("serviceStatus")
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as PlayerService.PlayerBinder
            player = binder.service
            serviceBound = true

            Toast.makeText(baseActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    private fun playAudio(media: String) {
        if (!serviceBound) {
            val intent = Intent(baseActivity, PlayerService::class.java)
            intent.putExtra("media", media)
            baseActivity.startService(intent)
            baseActivity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            baseActivity.unbindService(serviceConnection)
            player.stopSelf()
        }
    }

    private fun getSongData() {
        val resolver: ContentResolver = baseActivity.contentResolver
        val song: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? = resolver.query(song, null, selection, null, sortOrder)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id: Long = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val title: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val albumArtist: String = cursor.getString(cursor.getColumnIndexOrThrow("album_artist"))
                val track: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                val year: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
                val path: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                songList.add(Song(id, title, artist, album, albumArtist, track, year, path))
            }
            cursor.close()
            mAdapter.notifyDataSetChanged()
        }
    }
}
