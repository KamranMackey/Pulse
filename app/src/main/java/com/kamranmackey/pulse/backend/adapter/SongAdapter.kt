package com.kamranmackey.pulse.backend.adapter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.model.Song
import com.kamranmackey.pulse.backend.service.PlayerService
import com.kamranmackey.pulse.ui.dialogs.SongOptionsDialog
import com.kamranmackey.pulse.utils.MusicUtils
import com.kamranmackey.pulse.utils.StorageUtils
import com.kamranmackey.pulse.utils.extensions.showToast

class SongAdapter(
    private val songs: List<Song>,
    private val fm: FragmentManager
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    private var bPlayerServiceBound: Boolean = false
    private lateinit var mPlayerService: PlayerService

    init {
        this.run {
            setHasStableIds(true)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var artist: TextView = view.findViewById(R.id.artist)
        var options: TextView = view.findViewById(R.id.songOptions)
        var art: ImageView = view.findViewById(R.id.thumbnail)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder?) {
            val binder = service as PlayerService.PlayerBinder
            mPlayerService = binder.service
            bPlayerServiceBound = true
            mContext.showToast("Service Bound")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bPlayerServiceBound = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_row, parent, false)

        mContext = parent.context
        mPlayerService = PlayerService()

        return ViewHolder(itemView)
    }

    private fun playSelectedMedia(audioIndex: Int) {
        if (!bPlayerServiceBound) {
            val storage = StorageUtils(mContext)
            storage.storeAudio(songs)
            storage.storeAudioIndex(audioIndex)

            val playerIntent = Intent(mContext, PlayerService::class.java)
            mContext.startService(playerIntent)
            mContext.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        } else {
            val storage = StorageUtils(mContext)
            storage.storeAudioIndex(audioIndex)

            val broadcastIntent = Intent(PlayerService().playNewAudioBroadcast)
            mContext.sendBroadcast(broadcastIntent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        val string = SpannableStringBuilder()

        val title = song.title
        val id = song.albumId
        val artist = song.artist
        val album = song.album

        string.append("$artist • $album")

        holder.title.text = title
        holder.artist.text = string
        holder.options.setOnClickListener {
            val songOptionsDialog = SongOptionsDialog(song, fm)
            songOptionsDialog.show(fm, songOptionsDialog.tag)
        }

        holder.art.load(MusicUtils.getAlbumArtFromMediaStore(id, mContext)) {
            crossfade(true)
            transformations(
                RoundedCornersTransformation(
                    topLeft = 4f,
                    topRight = 4f,
                    bottomLeft = 4f,
                    bottomRight = 4f
                )
            )
        }

        holder.itemView.setOnClickListener {
            playSelectedMedia(holder.adapterPosition)
        }

        holder.itemView.setOnLongClickListener {
            val songOptionsDialog = SongOptionsDialog(song, fm)
            songOptionsDialog.show(fm, songOptionsDialog.tag)
            true
        }
    }

    override fun getItemCount(): Int = songs.size
}