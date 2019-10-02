package com.kamranmackey.pulse.backend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song


class SongAdapter(private val moviesList: List<Song>) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var artist: TextView = view.findViewById(R.id.year)
        var year: TextView = view.findViewById(R.id.genre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_row, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = moviesList[position]
        holder.title.text = movie.title
        holder.year.text = movie.artist
        holder.artist.text = movie.year.toString()
    }

    override fun getItemCount(): Int = moviesList.size
}