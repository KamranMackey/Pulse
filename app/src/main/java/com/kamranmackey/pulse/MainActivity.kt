package com.kamranmackey.pulse

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamranmackey.pulse.backend.adapter.ViewPagerAdapter
import com.kamranmackey.pulse.databinding.ActivityMainBinding
import com.kamranmackey.pulse.ui.fragments.*
import com.kamranmackey.pulse.utils.extensions.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavigationView: BottomNavigationView
    private lateinit var mViewPager: ViewPager
    private lateinit var mNotificationManager: NotificationManager
    private var prevMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannelGroup()
        createNotificationChannel()

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mViewPager = findViewById(R.id.viewpager)

        mNavigationView = findViewById(R.id.bottom_navigation)
        mNavigationView.setOnNavigationItemSelectedListener(this)

        mBinding.toolbarTitle.text = getString(R.string.action_songs)
        mBinding.toolbarTitle.setOnClickListener { showToast("Hello Title") }
        mBinding.toolbarTitle.setOnLongClickListener { showToast("Hello Long Click") }

        mViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.isChecked = false
                } else {
                    mNavigationView.menu.getItem(0).isChecked = false
                }

                mNavigationView.menu.getItem(position).isChecked = true
                toolbar_title.text = mNavigationView.menu.getItem(position).title

                prevMenuItem = mNavigationView.menu.getItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        initializeViewPager(mViewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_songs -> {
                mViewPager.currentItem = 0
                return true
            }
            R.id.action_albums -> {
                mViewPager.currentItem = 1
                return true
            }

            R.id.action_search -> {
                mViewPager.currentItem = 2
                return true
            }
            R.id.action_playlists -> {
                mViewPager.currentItem = 3
                return true
            }
            R.id.action_settings -> {
                mViewPager.currentItem = 4
                return true
            }
        }
        return false
    }

//    private fun loadFragment(fragment: Fragment) {
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.container, fragment)
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//        transaction.setCustomAnimations(
//            android.R.anim.fade_in,
//            android.R.anim.fade_out
//        )
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }

    private fun initializeViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager, 0)
        adapter.addFragment(SongsFragment())
        adapter.addFragment(AlbumsFragment())
        adapter.addFragment(SearchFragment())
        adapter.addFragment(PlaylistsFragment())
        adapter.addFragment(SettingsFragment())
        viewPager.adapter = adapter
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelGroup() {
        val name: CharSequence = "Media"
        val id = "com.kamranmackey.pulse.notifications.media"
        mNotificationManager.createNotificationChannelGroup(NotificationChannelGroup(id, name))
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name: CharSequence = "Media Playback"
        val description = "Playback notification used to control the playback of media."
        val importance: Int = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("com.kamranmackey.pulse.playback", name, importance)
        channel.description = description
        channel.group = "com.kamranmackey.pulse.notifications.media"
        channel.setSound(null, null)
        channel.enableVibration(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        mNotificationManager.createNotificationChannel(channel)
    }
}
