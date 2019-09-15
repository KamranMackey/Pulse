package com.kamranmackey.pulse.utils.extensions

import android.content.Context
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
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

    /**
     * Simple extension that allows the creation of popup menus.
     *
     * @return a [popup menu][android.widget.PopupMenu].
     */
    fun Context.popup(view: View, menu: Int) {
        val popupMenu = PopupMenu(applicationContext, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(menu, popupMenu.menu)
        popupMenu.show()
    }

}