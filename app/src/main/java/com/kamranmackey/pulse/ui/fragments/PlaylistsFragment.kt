package com.kamranmackey.pulse.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.kamranmackey.pulse.R

class PlaylistsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_playlists, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_playlists, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}