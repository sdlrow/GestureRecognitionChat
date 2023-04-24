package com.example.gesturerecognitionwebchat.base

import android.content.Context
import android.content.SharedPreferences

class PrefManager(private val context: Context) {
    companion object {
        const val PREF_NAME = "86c18f75c1ab897a626d29af4072d7e1"
        const val KEY_LOGIN = "f203a6710b2f602107ad1499fcd55d51"
        const val KEY_PASSWORD = "140be43beb5e395be0c9c9b3ec3d42b3"
    }

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isLoginAndPasswordExist(): Boolean {
        sharedPreferences.getString(KEY_LOGIN, null)?.let { l ->
            sharedPreferences.getString(KEY_PASSWORD, "")?.let { p ->
                if (l.isNotEmpty() && p.isNotEmpty()) return true
            }
        }
        return false
    }
}