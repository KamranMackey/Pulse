package com.kamranmackey.pulse.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song
import java.io.File

class SongDetailDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context: Context = requireContext()
        val song = arguments!!.getParcelable<Song>("song")

        val dialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
            .show {
                customView(R.layout.dialog_file_details, scrollable = true)
                positiveButton(android.R.string.ok)
                title(R.string.menu_song_details)
            }

        val dialogView = dialog.getCustomView()

        val name: TextView = dialogView.findViewById(R.id.fileName)
        val path: TextView = dialogView.findViewById(R.id.filePath)

        name.text = makeTextWithTitle(context, R.string.label_file_name, "-")
        path.text = makeTextWithTitle(context, R.string.label_file_path, "-")

        if (song != null) {
            val songFile = File(song.path)
            if (songFile.exists()) {
                name.text = makeTextWithTitle(context, R.string.label_file_name, songFile.name)
                path.text = makeTextWithTitle(context, R.string.label_file_path, songFile.absolutePath)
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


        private fun getHumanReadableFileSize(size: Long): String {
            val sizeInKilobytes = size / 1024
            val sizeInMegabytes = sizeInKilobytes / 2014
            return "$sizeInMegabytes MB"
        }
    }
}