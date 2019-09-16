package com.kamranmackey.pulse.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline val Fragment.baseActivity: FragmentActivity
    get() = activity ?: throw IllegalStateException("Fragment not attached")