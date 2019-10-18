package com.kamranmackey.pulse.backend.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Album
import com.kamranmackey.pulse.utils.extensions.showToast

class AlbumAdapter(private val albums: ArrayList<Album>): RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.name)
        var albumString: TextView = view.findViewById(R.id.albumString)
        var options: TextView = view.findViewById(R.id.albumOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_list_row, parent, false)

        context = parent.context

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]
        val string = SpannableStringBuilder()

        val title = album.title
        val artist = album.artist
        val tracks: Int = album.tracks

        val songString: String = if (tracks > 1) {
            "songs"
        } else {
            "song"
        }

        string.append(artist).append(" • ").append("$tracks $songString")

        holder.title.text = title
        holder.albumString.text = string
        holder.options.setOnClickListener {
            context.showToast("Hello Album Options")
        }
    }

    override fun getItemCount(): Int = albums.size

}