package com.kamranmackey.pulse.utils

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.kamranmackey.pulse.backend.models.Song

class StorageUtils(var context: Context) {
    private val storage = "com.kamranmackey.pulse.STORAGE"
    private var preferences: SharedPreferences? = null

    fun storeAudio(arrayList: ArrayList<Song>) {
        preferences = context.getSharedPreferences(storage, Context.MODE_PRIVATE)

        val editor = preferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(arrayList)
        editor.putString("audioArrayList", json)
        editor.apply()
    }

    fun loadAudio(): ArrayList<Song> {
        preferences = context.getSharedPreferences(storage, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences!!.getString("audioArrayList", null)
        val type = object : TypeToken<ArrayList<Song>>() {}.type
        return gson.fromJson<ArrayList<Song>>(json, type)
    }

    fun storeAudioIndex(index: Int) {
        preferences = context.getSharedPreferences(storage, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt("audioIndex", index)
        editor.apply()
    }

    fun loadAudioIndex(): Int {
        preferences = context.getSharedPreferences(storage, Context.MODE_PRIVATE)
        return preferences!!.getInt("audioIndex", -1)//return -1 if no data found
    }

    fun clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(storage, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.clear()
        editor.apply()
    }
}