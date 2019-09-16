package com.kamranmackey.pulse.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.kamranmackey.pulse.R

class AboutDialog : DialogFragment() {

    companion object {
        private const val tag = "AboutDialog"

        fun show(activity: FragmentActivity) = AboutDialog().show(activity.supportFragmentManager, tag)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(activity!!).show {
            title(R.string.about_dialog_title)
            message(R.string.about_dialog_body) {
                html()
                lineSpacing(1.4f)
            }
            positiveButton(R.string.about_dialog_dismiss)
            onDismiss {
                this@AboutDialog.dismiss()
            }
        }
    }

}