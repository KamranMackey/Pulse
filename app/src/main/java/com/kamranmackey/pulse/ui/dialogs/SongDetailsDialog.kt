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
import com.kamranmackey.pulse.backend.model.Song
import com.kamranmackey.pulse.utils.MusicUtils
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.text.NumberFormat

class SongDetailsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context: Context = requireContext()
        val song = arguments!!.getParcelable<Song>("song")

        val numberFormat: NumberFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NumberFormat.getInstance(resources.configuration.locales.get(0))
        } else {
            NumberFormat.getInstance(resources.configuration.locale)
        }

        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))
            .show {
                customView(R.layout.dialog_file_details, scrollable = false)
                positiveButton(android.R.string.ok)
                title(R.string.dialog_song_options_details_title)
            }

        val view = dialog.getCustomView()

        val name: TextView = view.findViewById(R.id.fileName)
        val path: TextView = view.findViewById(R.id.filePath)
        val size: TextView = view.findViewById(R.id.fileSize)
        val format: TextView = view.findViewById(R.id.fileFormat)
        val formatAlgorithm: TextView = view.findViewById(R.id.fileFormatAlgorithm)
        val channels: TextView = view.findViewById(R.id.fileChannels)
        val bitrate: TextView = view.findViewById(R.id.fileBitrate)
        val bitrateType: TextView = view.findViewById(R.id.fileBitrateType)
        val sampleRate: TextView = view.findViewById(R.id.fileSampleRate)
        val length: TextView = view.findViewById(R.id.fileLength)
        val samples: TextView = view.findViewById(R.id.fileSamples)
        val encoder: TextView = view.findViewById(R.id.fileEncoder)

        val title: TextView = view.findViewById(R.id.songTitle)
        val artist: TextView = view.findViewById(R.id.songArtist)
        val composer: TextView = view.findViewById(R.id.songComposer)
        val album: TextView = view.findViewById(R.id.songAlbum)
        val year: TextView = view.findViewById(R.id.songYear)

        name.text = makeTextWithTitle(context, R.string.label_file_name, "-")
        path.text = makeTextWithTitle(context, R.string.label_file_path, "-")
        size.text = makeTextWithTitle(context, R.string.label_file_size, "-")
        format.text = makeTextWithTitle(context, R.string.label_file_format, "-")
        formatAlgorithm.text = makeTextWithTitle(context, R.string.label_file_format_algorithm, "-")
        channels.text = makeTextWithTitle(context, R.string.label_file_channels, "-")
        bitrate.text = makeTextWithTitle(context, R.string.label_file_bitrate, "-")
        bitrateType.text = makeTextWithTitle(context, R.string.label_file_bitrate_type, "-")
        sampleRate.text = makeTextWithTitle(context, R.string.label_file_sample_rate, "-")
        length.text = makeTextWithTitle(context, R.string.label_file_length, "-")
        samples.text = makeTextWithTitle(context, R.string.label_file_samples, "-")
        encoder.text = makeTextWithTitle(context, R.string.label_file_encoder, "-")

        title.text = makeTextWithTitle(context, R.string.label_song_title, "-")
        artist.text = makeTextWithTitle(context, R.string.label_song_artist, "-")
        composer.text = makeTextWithTitle(context, R.string.label_song_composer, "-")
        album.text = makeTextWithTitle(context, R.string.label_song_album, "-")
        year.text = makeTextWithTitle(context, R.string.label_song_year, "-")

        if (song != null) {
            val songFile = File(song.path)
            if (songFile.exists()) {
                name.text = makeTextWithTitle(context, R.string.label_file_name, songFile.name)
                path.text = makeTextWithTitle(context, R.string.label_file_path, songFile.absolutePath)
                size.text = makeTextWithTitle(context, R.string.label_file_size, getFileSizeString(songFile.length()))
                try {
                    val file = AudioFileIO.read(songFile)
                    val tag = file.tag
                    val header = file.audioHeader
                    val fileFormat = header.format
                    val fileFormatAlgorithm = if (header.isLossless) "Lossless" else "Lossy"
                    val fileChannels = header.channels
                    val fileBitrate = header.bitRate
                    val fileBitrateType = if (header.isVariableBitRate) "Variable" else "Constant"
                    val fileSampleRate = header.sampleRate
                    val fileLength = MusicUtils.getSongDuration(header.trackLength * 1000)
                    val fileSamples = numberFormat.format(header.noOfSamples)
                    val fileEncoder = tag.getFirst(FieldKey.ENCODER).toString()

                    if (fileEncoder.isEmpty()) {
                        encoder.visibility = View.GONE
                    } else {
                        encoder.text = makeTextWithTitle(context, R.string.label_file_encoder, fileEncoder)
                    }

                    val songTitle = tag.getFirst(FieldKey.TITLE).toString()
                    val songArtist = tag.getFirst(FieldKey.ARTIST).toString()
                    val songComposer = tag.getAll(FieldKey.COMPOSER).joinToString(", ")
                    val songAlbum: String = tag.getFirst(FieldKey.ALBUM)
                    val songYear: String = tag.getFirst(FieldKey.YEAR)

                    format.text = makeTextWithTitle(context, R.string.label_file_format, fileFormat)
                    formatAlgorithm.text = makeTextWithTitle(context, R.string.label_file_format_algorithm, fileFormatAlgorithm)
                    channels.text = makeTextWithTitle(context, R.string.label_file_channels, fileChannels)
                    bitrate.text = makeTextWithTitle(context, R.string.label_file_bitrate, "$fileBitrate kb/s")
                    bitrateType.text = makeTextWithTitle(context, R.string.label_file_bitrate_type, fileBitrateType)
                    sampleRate.text = makeTextWithTitle(context, R.string.label_file_sample_rate, "$fileSampleRate Hz")
                    length.text = makeTextWithTitle(context, R.string.label_file_length, fileLength)
                    samples.text = makeTextWithTitle(context, R.string.label_file_samples, "$fileSamples samples")
                    title.text = makeTextWithTitle(context, R.string.label_song_title, songTitle)
                    artist.text = makeTextWithTitle(context, R.string.label_song_artist, songArtist)

                    if (songComposer.isEmpty()) {
                        composer.visibility = View.GONE
                    } else {
                        composer.text =
                            makeTextWithTitle(context, R.string.label_song_composer, songComposer)
                    }

                    album.text = makeTextWithTitle(context, R.string.label_song_album, songAlbum)
                    year.text = makeTextWithTitle(context, R.string.label_song_year, songYear)

                } catch (@NonNull e: CannotReadException) {
                    Log.e("Audio File Read Error", "Could not read file", e)
                }
            }
        }

        return dialog
    }

    companion object {

        fun create(song: Song): SongDetailsDialog {
            val dialog = SongDetailsDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }

        @Suppress("DEPRECATION")
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