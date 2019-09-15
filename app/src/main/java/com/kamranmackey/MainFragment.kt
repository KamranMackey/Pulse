package com.kamranmackey

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.kamranmackey.pulse.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

}
