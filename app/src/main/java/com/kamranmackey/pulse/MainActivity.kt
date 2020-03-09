package com.kamranmackey.pulse

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamranmackey.pulse.databinding.ActivityMainBinding
import com.kamranmackey.pulse.ui.fragments.AlbumsFragment
import com.kamranmackey.pulse.ui.fragments.SearchFragment
import com.kamranmackey.pulse.ui.fragments.SettingsFragment
import com.kamranmackey.pulse.ui.fragments.SongsFragment
import com.kamranmackey.pulse.utils.extensions.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        createNotificationChannel() // create notification channel
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(this)

        binding.toolbarTitle.text = getString(R.string.action_songs)
        binding.toolbarTitle.setOnClickListener { showToast("Hello Title") }
        binding.toolbarTitle.setOnLongClickListener { showToast("Hello Long Click") }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SongsFragment())
                .commitNow()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_songs -> {
                toolbar_title.text = getString(R.string.action_songs)
                loadFragment(SongsFragment.newInstance())
                return true
            }
            R.id.action_albums -> {
                toolbar_title.text = getString(R.string.action_albums)
                loadFragment(AlbumsFragment.newInstance())
                return true
            }
            R.id.action_settings -> {
                toolbar_title.text = getString(R.string.action_settings)
                loadFragment(SettingsFragment())
                return true
            }
            R.id.action_search -> {
                toolbar_title.text = getString(R.string.action_search)
                loadFragment(SearchFragment())
                return true
            }
        }
        return false
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name: CharSequence = "Media Playback"
        val description = "Playback notification used to control the playback of media."
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("com.kamranmackey.pulse.playback", name, importance)
        channel.description = description
        channel.setSound(null, null)
        channel.enableVibration(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager: NotificationManager? =
            getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}
