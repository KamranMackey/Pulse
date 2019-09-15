package com.kamranmackey.pulse

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kamranmackey.MainFragment
import com.kamranmackey.pulse.databinding.ActivityMainBinding
import com.kamranmackey.pulse.utils.extensions.Context.popup
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
        binding.overflowMenu.setOnClickListener { popup(binding.overflowMenu, R.menu.menu_main)}

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    private fun showOverflow(view: View) {

    }
}
