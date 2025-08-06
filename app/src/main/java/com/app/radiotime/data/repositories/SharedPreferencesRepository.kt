package com.app.radiotime.data.repositories

import android.content.SharedPreferences
import com.app.radiotime.utils.Constants.COUNTRY_CODE
import com.app.radiotime.utils.Constants.IS_FIRST_START
import com.app.radiotime.utils.Constants.LAST_PLAY
import com.app.radiotime.utils.Constants.MODE
import com.app.radiotime.utils.Constants.HAS_PURCHASED
import com.app.radiotime.utils.Constants.VERSION
import com.app.radiotime.utils.ThemeMode

class SharedPreferencesRepository(private val sharedPreferences: SharedPreferences) {
    fun getCountryCode(): String? {
        return sharedPreferences.getString(COUNTRY_CODE, "US")
    }

    fun isFirstStartUp(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_START, true)
    }

    fun setUserCountry(code: String) {
        sharedPreferences.edit().apply {
            putString(COUNTRY_CODE, code)
            putBoolean(IS_FIRST_START, false)
            apply()
        }
    }

    fun setLastPlayID(stationId: String) {
        sharedPreferences.edit().apply {
            putString(LAST_PLAY, stationId)
            apply()
        }
    }

    fun getLastPlayID(): String? {
        return sharedPreferences.getString(LAST_PLAY, "")
    }

    fun setThemeMode(mode: Int) {
        sharedPreferences.edit().apply {
            putInt(MODE, mode)
            apply()
        }
    }

    fun getThemeMode(): Int {
        return sharedPreferences.getInt(MODE, ThemeMode.AUTO.id)
    }

    fun setDBVersion(version: Int) {
        sharedPreferences.edit().apply {
            putInt(VERSION, version)
            apply()
        }
    }

    fun getDBVersion(): Int {
        return sharedPreferences.getInt(VERSION, 1)
    }

    fun setHasPurchased(hasPurchased: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(HAS_PURCHASED, hasPurchased)
            apply()
        }
    }

    fun getHasPurchased(): Boolean {
        return sharedPreferences.getBoolean(HAS_PURCHASED, true)
    }
}