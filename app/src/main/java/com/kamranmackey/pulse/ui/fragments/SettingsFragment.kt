package com.kamranmackey.pulse.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.ui.dialogs.AboutDialog
import com.kamranmackey.pulse.utils.extensions.baseActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                AboutDialog.show(baseActivity)
            }
        }
        return true
    }

}
