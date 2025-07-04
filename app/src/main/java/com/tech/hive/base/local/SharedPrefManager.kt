package com.tech.hive.base.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.tech.hive.data.model.LoginData
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val LOGIN_RESPONSE = "loginResponse"
        const val USER_TOKEN = "userToken"
        const val USER_PROFILE = "UserProfile"
    }

    fun setLoginData(bean: LoginData) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.LOGIN_RESPONSE, Gson().toJson(bean))
        editor.apply()
    }


    fun getLoginData(): LoginData? {
        val s: String? = sharedPreferences.getString(KEY.LOGIN_RESPONSE, null)
        return Gson().fromJson(s, LoginData::class.java)
    }

    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        val token: String? = sharedPreferences.getString(KEY.USER_TOKEN, null)
        return token
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}