package com.kamranmackey.pulse.utils.extensions

import android.content.Context
import android.widget.Toast

/**
 * Simplifies the creation of an Android toast requiring only the
 * text of the Toast, and optionally, the length.
 *
 * @param text The text to display in the toast.
 * @param length The amount of time to remain on screen.
 * @return a [toast][android.widget.Toast].
 */
fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT): Boolean {
    Toast.makeText(this, text, duration).show()
    return true
}