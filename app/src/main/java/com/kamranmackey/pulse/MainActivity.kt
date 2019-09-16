package com.kamranmackey.pulse

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kamranmackey.pulse.databinding.ActivityMainBinding
import com.kamranmackey.pulse.utils.extensions.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.account.setOnClickListener { showToast("Hello Account") }
        binding.toolbarTitle.setOnClickListener { showToast("Hello Title") }
        binding.toolbarTitle.setOnLongClickListener { showToast("Hello Long Click") }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
