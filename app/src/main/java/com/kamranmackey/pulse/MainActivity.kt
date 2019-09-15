package com.kamranmackey.pulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamranmackey.pulse.databinding.ActivityMainBinding
import com.kamranmackey.pulse.utils.extensions.Context.toast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarTitle.setOnLongClickListener { toast("Hello Long Click") }
        binding.searchAction.setOnClickListener { toast("Hello Search") }
        binding.settingsAction.setOnClickListener { toast("Hello Settings") }
    }
}
