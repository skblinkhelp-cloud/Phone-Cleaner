package com.sk.phonecleaner_aicleaner.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("phone_cleaner_prefs", Context.MODE_PRIVATE)

    fun setPrivacyAccepted(accepted: Boolean) {
        sharedPreferences.edit().putBoolean("privacy_accepted", accepted).apply()
    }

    fun isPrivacyAccepted(): Boolean {
        return sharedPreferences.getBoolean("privacy_accepted", false)
    }

    fun setShowHiddenFiles(show: Boolean) {
        sharedPreferences.edit().putBoolean("show_hidden_files", show).apply()
    }

    fun isShowHiddenFiles(): Boolean {
        return sharedPreferences.getBoolean("show_hidden_files", false)
    }
}