package com.kamranmackey.pulse.backend.adapters

import android.content.Context
import android.media.MediaPlayer
import android.text.SpannableStringBuilder
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.ui.dialogs.SongDetailDialog
import com.kamranmackey.pulse.utils.extensions.showToast

class SongAdapter(private val songs: ArrayList<Song>,
                  private val fm: FragmentManager) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mPlayer: MediaPlayer

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var artist: TextView = view.findViewById(R.id.artist)
        var options: TextView = view.findViewById(R.id.songOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_row, parent, false)

        mContext = parent.context
        mPlayer = MediaPlayer()

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        val string = SpannableStringBuilder()

        val title = song.title
        val artist = song.albumArtist
        val album = song.album
        val path = song.path

        string.append(artist).append(" • ").append(album)

        holder.title.text = title
        holder.artist.text = string
        holder.options.setOnClickListener { it ->
            val menu = PopupMenu(mContext, holder.options)
            menu.inflate(R.menu.menu_song)
            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.details -> {
                        SongDetailDialog.create(song).show(fm, "SONG_DETAILS")
                    }
                    R.id.delete_from_device -> {
                        mContext.showToast("Not yet implemented")
                    }
                }
                true
            }
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            menu.show()
        }

        holder.itemView.setOnClickListener {
            mPlayer.reset()
            mPlayer.setDataSource(path)
            mPlayer.prepare()
            mPlayer.start()
            mContext.showToast("$title by $artist now playing!")
        }

        holder.itemView.setOnLongClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    override fun getItemCount(): Int = songs.size
}