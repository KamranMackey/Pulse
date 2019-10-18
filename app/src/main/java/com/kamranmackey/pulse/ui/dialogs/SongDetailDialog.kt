package com.kamranmackey.pulse.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.utils.MusicUtils
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.text.NumberFormat

class SongDetailDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx: Context = requireContext()
        val song = arguments!!.getParcelable<Song>("song")
        var numberFormat: NumberFormat? = null

        numberFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NumberFormat.getInstance(resources.configuration.locales.get(0))
        } else {
            NumberFormat.getInstance(resources.configuration.locale)
        }

        val dialog = MaterialDialog(ctx, BottomSheet(LayoutMode.MATCH_PARENT))
            .show {
                customView(R.layout.dialog_file_details, scrollable = true)
                positiveButton(android.R.string.ok)
                title(R.string.title_song_details)
            }

        val view = dialog.getCustomView()

        // File Fields
        val name: TextView = view.findViewById(R.id.fileName)
        val path: TextView = view.findViewById(R.id.filePath)
        val size: TextView = view.findViewById(R.id.fileSize)
        val format: TextView = view.findViewById(R.id.fileFormat)
        val channels: TextView = view.findViewById(R.id.fileChannels)
        val bitrate: TextView = view.findViewById(R.id.fileBitrate)
        val sampleRate: TextView = view.findViewById(R.id.fileSampleRate)
        val length: TextView = view.findViewById(R.id.fileLength)
        val comment: TextView = view.findViewById(R.id.fileEncoder)
        // Song Fields
        val title: TextView = view.findViewById(R.id.songTitle)
        val artist: TextView = view.findViewById(R.id.songArtist)
        val album: TextView = view.findViewById(R.id.songAlbum)
        val year: TextView = view.findViewById(R.id.songYear)

        name.text = makeTextWithTitle(ctx, R.string.label_file_name, "-")
        path.text = makeTextWithTitle(ctx, R.string.label_file_path, "-")
        size.text = makeTextWithTitle(ctx, R.string.label_file_size, "-")
        format.text = makeTextWithTitle(ctx, R.string.label_file_format, "-")
        channels.text = makeTextWithTitle(ctx, R.string.label_file_channels, "-")
        bitrate.text = makeTextWithTitle(ctx, R.string.label_file_bitrate, "-")
        sampleRate.text = makeTextWithTitle(ctx, R.string.label_file_samplerate, "-")
        length.text = makeTextWithTitle(ctx, R.string.label_file_length, "-")
        comment.text = makeTextWithTitle(ctx, R.string.label_file_encoder, "-")

        title.text = makeTextWithTitle(ctx, R.string.label_song_title, "-")
        artist.text = makeTextWithTitle(ctx, R.string.label_song_artist, "-")
        album.text = makeTextWithTitle(ctx, R.string.label_song_album, "-")
        year.text = makeTextWithTitle(ctx, R.string.label_song_year, "-")

        if (song != null) {
            val songFile = File(song.path)
            if (songFile.exists()) {
                name.text = makeTextWithTitle(ctx, R.string.label_file_name, songFile.name)
                path.text = makeTextWithTitle(ctx, R.string.label_file_path, songFile.absolutePath)
                size.text = makeTextWithTitle(ctx, R.string.label_file_size, getFileSizeString(songFile.length()))
                try {
                    val file = AudioFileIO.read(songFile)
                    val tag: Tag = file.tag
                    val header = file.audioHeader
                    val fileFormat = header.format
                    val fileChannels = header.channels
                    val fileBitrate = header.bitRate
                    val fileSampleRate = header.sampleRate
                    val fileLength = MusicUtils.getSongDuration(header.trackLength * 1000)
                    val fileSamples = numberFormat.format(header.noOfSamples)
                    val fileEncoder = tag.getFirst(FieldKey.ENCODER).toString()

                    val songTitle = tag.getFirst(FieldKey.TITLE).toString()
                    val songArtist = tag.getFirst(FieldKey.ARTIST).toString()
                    val songAlbum = tag.getFirst(FieldKey.ALBUM).toString()
                    val songYear = tag.getFirst(FieldKey.YEAR).toString()

                    val isLossless: String?
                    val bitRateType: String?

                    isLossless = if (header.isLossless) {
                        "lossless codec"
                    } else {
                        "lossy codec"
                    }

                    bitRateType = if (header.isVariableBitRate) {
                        "variable bitrate"
                    } else {
                        "constant bitrate"
                    }

                    format.text = makeTextWithTitle(ctx, R.string.label_file_format, "$fileFormat ($isLossless)")
                    channels.text = makeTextWithTitle(ctx, R.string.label_file_channels, fileChannels)
                    bitrate.text = makeTextWithTitle(ctx, R.string.label_file_bitrate, "$fileBitrate kb/s ($bitRateType)")
                    sampleRate.text = makeTextWithTitle(ctx, R.string.label_file_samplerate, "$fileSampleRate Hz")
                    length.text = makeTextWithTitle(ctx, R.string.label_file_length, "$fileLength ($fileSamples samples)")

                    title.text = makeTextWithTitle(ctx, R.string.label_song_title, songTitle)
                    artist.text = makeTextWithTitle(ctx, R.string.label_song_artist, songArtist)
                    album.text = makeTextWithTitle(ctx, R.string.label_song_album, songAlbum)
                    year.text = makeTextWithTitle(ctx, R.string.label_song_year, songYear)

                    if (fileEncoder.isEmpty()) {
                        comment.visibility = View.GONE
                    } else {
                        comment.text = makeTextWithTitle(ctx, R.string.label_file_encoder, fileEncoder)
                    }

                } catch (@NonNull e: CannotReadException) {
                    Log.e("Audio File Read Error", "Could not read file", e)
                }
            }
        }

        return dialog
    }

    companion object {

        fun create(song: Song): SongDetailDialog {
            val dialog = SongDetailDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }

        private fun makeTextWithTitle(context: Context, titleResId: Int, text: String?): Spanned {
            return Html.fromHtml("<b>" + context.resources.getString(titleResId) + ": " + "</b>" + text)
        }


        private fun getFileSizeString(sizeInBytes: Long): String {
            val fileSizeInKB = sizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            return "$fileSizeInMB MB"
        }
    }
}