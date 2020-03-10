package com.kamranmackey.pulse.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.afollestad.materialdialogs.LayoutMode.WRAP_CONTENT
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.utils.extensions.showToast

class SongOptionsDialog(private val song: Song,
                        private val fm: FragmentManager) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val dialog = MaterialDialog(context, BottomSheet(WRAP_CONTENT)).show {
            customView(R.layout.dialog_song_options, scrollable = false)
            title(R.string.song_options_title)
        }

        val view = dialog.getCustomView()

        val songDetails: LinearLayout = view.findViewById(R.id.song_options_details)
        val deleteFromDevice: LinearLayout = view.findViewById(R.id.song_options_delete)

        songDetails.setOnClickListener {
            this.dismiss()
            SongDetailsDialog.create(song).show(fm, "SONG_DETAILS")
        }

        deleteFromDevice.setOnClickListener {
            context.showToast("Not yet implemented...sorry!")
        }

        return dialog
    }
}