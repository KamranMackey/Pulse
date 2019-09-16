package com.kamranmackey.pulse

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.kamranmackey.pulse.ui.dialogs.AboutDialog
import com.kamranmackey.pulse.utils.extensions.baseActivity

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
