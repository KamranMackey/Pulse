package com.kamranmackey.pulse.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline val Fragment.baseActivity: FragmentActivity
    get() = activity ?: throw IllegalStateException("Fragment not attached")

fun FragmentManager.switch(containerId: Int, newFrag: Fragment, tag: String) {

    var current = findFragmentByTag(tag)
    beginTransaction()
        .apply {

            // Hide the current fragment
            primaryNavigationFragment?.let { hide(it) }

            // Check if current fragment exists in fragmentManager
            if (current == null) {
                current = newFrag
                add(containerId, current!!, tag)
            } else {
                show(current!!)
            }
        }
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .setPrimaryNavigationFragment(current)
        .setReorderingAllowed(true)
        .commitNowAllowingStateLoss()
}