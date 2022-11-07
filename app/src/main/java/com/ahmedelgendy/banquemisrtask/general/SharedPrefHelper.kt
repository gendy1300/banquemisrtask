package com.ahmedelgendy.banquemisrtask.general

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPrefHelper @Inject constructor(@ApplicationContext context: Context) {

    private val PREFS_NAME = "banquemisrtask"

    val editor: SharedPreferences.Editor


    private var sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun put(key: String, value: String) {
        editor.putString(key, value)
            .apply()
    }

    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value)
            .apply()
    }

    fun put(key: String, value: Int) {
        editor.putInt(key, value)
            .apply()
    }


    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun getInt(key: String): String? {
        return sharedPref.getString(key, null)
    }


    fun clear() {
        editor.clear()
            .apply()
    }

}
