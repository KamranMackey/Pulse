package com.kamranmackey.pulse.backend.adapters

import android.media.MediaPlayer
import android.text.SpannableStringBuilder
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song

class SongAdapter(private val songList: List<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private lateinit var mPlayer: MediaPlayer

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var artist: TextView = view.findViewById(R.id.artist)
        var options: TextView = view.findViewById(R.id.songOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_row, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList[position]
        val string = SpannableStringBuilder()

        mPlayer = MediaPlayer()

        val title = song.title
        val artist = song.albumArtist
        val album = song.album
        val path = song.path

        string.append(artist).append(" • ").append(album)

        holder.title.text = title
        holder.artist.text = string
        holder.options.setOnClickListener {
            val menu = PopupMenu(holder.options.context, holder.options)
            menu.inflate(R.menu.menu_song)
            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.details -> {
                        Toast.makeText(holder.options.context, "Song path: $path",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            menu.show()
        }

        holder.itemView.setOnClickListener {
            mPlayer.reset()
            mPlayer.setDataSource(path)
            mPlayer.prepare()
            mPlayer.start()
        }

        holder.itemView.setOnLongClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK):q
        }
    }

    override fun getItemCount(): Int = songList.size
}