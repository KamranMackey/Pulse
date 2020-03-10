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
import com.kamranmackey.pulse.backend.adapter.SongAdapter
import com.kamranmackey.pulse.backend.loader.songs.SongLoader
import com.kamranmackey.pulse.backend.model.Song
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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val songs: List<Song> = SongLoader.getAllSongs(mContext)
        val adapter = SongAdapter(songs, mManager)

        val layoutManager: LayoutManager = LinearLayoutManager(mContext)

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
