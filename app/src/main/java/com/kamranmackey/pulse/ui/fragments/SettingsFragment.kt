package com.kamranmackey.pulse.ui.fragments

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.utils.extensions.baseActivity
import com.kamranmackey.pulse.utils.extensions.showToast

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val requestEqualizerCode = 1000

        val equalizer = findPreference("equalizer") as Preference?

        equalizer!!.setOnPreferenceClickListener {
            val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)

            if (intent.resolveActivity(baseActivity.packageManager) != null) {
                startActivityForResult(intent, requestEqualizerCode)
            } else {
                baseActivity.showToast("No system equalizer available on this device.")
            }

            true
        }

    }

}
