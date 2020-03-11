package com.kamranmackey.pulse.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.adapter.AlbumAdapter
import com.kamranmackey.pulse.backend.loader.albums.AlbumLoader
import com.kamranmackey.pulse.backend.model.Album
import com.kamranmackey.pulse.utils.extensions.baseActivity

class AlbumsFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.albums_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.albumRecyclerView)
        mContext = baseActivity
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val albumList: List<Album> = AlbumLoader.getAllAlbums(mContext)
        val albumAdapter = AlbumAdapter(albumList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mContext)

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.adapter = albumAdapter
    }

}
