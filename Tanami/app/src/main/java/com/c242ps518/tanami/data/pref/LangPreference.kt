package com.c242ps518.tanami.data.pref

import android.content.Context
import android.content.SharedPreferences

class LangPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)

    companion object {
        const val LANGUAGE_KEY = "language_key"
        const val DEFAULT_LANGUAGE = "en"
    }

    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(LANGUAGE_KEY, language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
}
