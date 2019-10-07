package com.kamranmackey.pulse.backend.adapters


import android.media.MediaMetadataRetriever
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.utils.MusicUtils

class SongAdapter(private val songList: List<Song>) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var artist: TextView = view.findViewById(R.id.artist)
        var year: TextView = view.findViewById(R.id.year)
        // var art: ImageView = view.findViewById(R.id.thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_row, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList[position]
        val string = SpannableStringBuilder()

        val title = song.title
        val artist = song.albumArtist
        val album = song.album
        val year = song.year.toString()

        string.append(artist).append(" • ").append(album)

        holder.title.text = title
        holder.artist.text = string
        holder.year.text = year
    }

    override fun getItemCount(): Int = songList.size
}