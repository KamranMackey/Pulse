package com.kamranmackey.pulse.utils.extensions

import android.content.Context
import android.widget.Toast

object Context {

    /**
     * Simplifies the creation of an Android toast requiring only the
     * text of the Toast, and optionally, the length.
     *
     * @param message The message to be displayed.
     * @param length The amount of time to remain on screen.
     * @return a [toast][android.widget.Toast].
     */
    fun Context.toast(message: CharSequence, length: Int = 0): Boolean {
        Toast.makeText(this, message, length).show()
        return true
    }

}