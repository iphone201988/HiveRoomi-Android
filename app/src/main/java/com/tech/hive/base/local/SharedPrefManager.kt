package com.tech.hive.base.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.tech.hive.data.model.LoginData
import javax.inject.Inject
import androidx.core.content.edit
import com.tech.hive.base.utils.saveValue

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val LOGIN_RESPONSE = "loginResponse"
        const val USER_TOKEN = "userToken"
        const val USER_PROFILE = "UserProfile"
        const val USER_ID = "userId"
        const val ROLE_TYPE = "roleType"
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


    fun saveSide(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER_ID, token)
        editor.apply()
    }

    fun getSide(): String? {
        val token: String? = sharedPreferences.getString(KEY.USER_ID, "1")
        return token
    }



    fun saveRole(type: Int) {
        sharedPreferences.saveValue(KEY.ROLE_TYPE, type)
    }

    fun getRole(): Int {
        return sharedPreferences.getInt(KEY.ROLE_TYPE, 1)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}