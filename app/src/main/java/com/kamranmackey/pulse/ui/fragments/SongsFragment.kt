package com.kamranmackey.pulse.ui.fragments

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast

import androidx.fragment.app.Fragment
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
import com.kamranmackey.pulse.utils.listeners.recyclerview.OnTouchListener
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import java.util.ArrayList


class SongsFragment : Fragment() {

    private val songList = ArrayList<Song>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: SongAdapter
    private lateinit var mContext: Context
    private lateinit var mVibrator: Vibrator
    private lateinit var mPlayer: MediaPlayer

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

        recyclerView = view.findViewById(R.id.recyclerView)
        mAdapter = SongAdapter(songList)
        mContext = baseActivity
        mVibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mPlayer = MediaPlayer()

        val layoutManager: LayoutManager = LinearLayoutManager(mContext)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                mContext,
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView.adapter = mAdapter
        recyclerView.addOnItemTouchListener(
            OnTouchListener(
                mContext,
                recyclerView,
                object : OnTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val title: String = songList[position].title
                        val artist: String = songList[position].artist
                        val path: String = songList[position].path

                        Toast.makeText(
                            baseActivity,
                            "$title by $artist selected!",
                            Toast.LENGTH_SHORT
                        ).show()

                        mPlayer.reset()
                        mPlayer.setDataSource(path)
                        mPlayer.prepare()
                        mPlayer.start()
                        Log.d("SongFragment", mPlayer.trackInfo.toString())
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        val title: String = songList[position].title
                        val artist: String = songList[position].artist
                        Toast.makeText(
                            baseActivity,
                            "$title by $artist long clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        )

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
