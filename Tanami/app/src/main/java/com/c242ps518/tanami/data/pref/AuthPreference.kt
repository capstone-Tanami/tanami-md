package com.c242ps518.tanami.data.pref

import android.content.Context
import android.content.SharedPreferences

class AuthPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_preference", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "token"
        private const val NAME_KEY = "name"
        private const val ID_KEY = "id"
    }

    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun saveName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString(NAME_KEY, name)
        editor.apply()
    }

    fun getName(): String? {
        return sharedPreferences.getString(NAME_KEY, null)
    }

    fun saveId(id: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(ID_KEY, id)
        editor.apply()
    }

    fun getId(): Int? {
        val id = sharedPreferences.getInt(ID_KEY, -1)
        return if (id != -1) id else null
    }

//    fun saveId(id: Int) {
//        val editor = sharedPreferences.edit()
//        editor.putInt(ID_KEY, id)
//        editor.apply()
//    }
//
//    fun getId(): Int? {
//        val id = sharedPreferences.getInt(ID_KEY, -1)
//        return if (id != -1) id else null
//    }
//
//    fun saveUsername(username: String) {
//        val editor = sharedPreferences.edit()
//        editor.putString(USERNAME_KEY, username)
//        editor.apply()
//    }
//
//    fun getUsername(): String? {
//        return sharedPreferences.getString(USERNAME_KEY, null)
//    }

    fun clearAuthData() {
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN_KEY)
        editor.remove(NAME_KEY)
        editor.remove(ID_KEY)
        editor.apply()
    }
}
