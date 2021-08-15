package com.nischal.clothingstore.data.prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.nischal.clothingstore.ui.models.UserDetails

class PrefsManager(private val gson: Gson, private val pref: SharedPreferences) {

    fun isLoginSkipped(): Boolean{
        return pref.getBoolean(PrefManagerKey.IS_LOGIN_SKIPPED, false)
    }

    fun setIsLoginSkipped(status: Boolean){
        pref.edit().apply {
            putBoolean(PrefManagerKey.IS_LOGIN_SKIPPED, status)
            apply()
        }
    }

    fun setProfileInfo(userInfo: UserDetails) {
        val json = gson.toJson(userInfo)
        pref.edit().apply {
            putString(PrefManagerKey.PROFILE_INFO, json)
            apply()
        }
    }

    fun getProfileInfo(): UserDetails {
        val data = pref.getString(PrefManagerKey.PROFILE_INFO, "")
        return gson.fromJson(data, UserDetails::class.java)
    }

    fun clearData() {
        pref.edit().clear().apply()
    }

    fun getToken(): String?{
        return pref.getString(PrefManagerKey.KEY_TOKEN, null)
    }

    fun setToken(token: String){
        pref.edit().apply{
            putString(PrefManagerKey.KEY_TOKEN, token)
            apply()
        }
    }

    fun removeToken(){
        pref.edit().apply{
            remove(PrefManagerKey.KEY_TOKEN)
            apply()
        }
    }

    private fun putStringValue(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    private fun getStringValue(key: String): String? {
        return pref.getString(key, "")
    }

    private fun putFloatValue(key: String, value: Float) {
        pref.edit().putFloat(key, value).apply()
    }

    private fun getFloatValue(key: String): Float? {
        return pref.getFloat(key, 0.00f)
    }

    private fun putBooleanValue(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    private fun getBooleanValue(key: String): Boolean {
        return pref.getBoolean(key, false)
    }
}

object PrefManagerKey {
    const val IS_LOGIN_SKIPPED = "IS_LOGIN_SKIPPED"
    const val PROFILE_INFO = "PROFILE_INFO"
    const val IS_LOGGED_IN = "is_logged_in"
    const val KEY_TOKEN = "TOKEN"
}