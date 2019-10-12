package com.kamranmackey.pulse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamranmackey.pulse.databinding.ActivityMainBinding
import com.kamranmackey.pulse.ui.fragments.SongsFragment
import com.kamranmackey.pulse.ui.fragments.SearchFragment
import com.kamranmackey.pulse.ui.fragments.SettingsFragment
import com.kamranmackey.pulse.utils.extensions.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(this)

        binding.toolbarTitle.text = getString(R.string.action_home)
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
            R.id.action_home -> {
                toolbar_title.text = getString(R.string.action_home)
                loadFragment(SongsFragment())
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
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
