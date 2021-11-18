package com.mab.mojoapp.utils

import android.content.Context
import android.content.SharedPreferences

object UPersistence {

    private const val SCOPE_USER = "user"
    private const val SCOPE_APP = "app"

    private lateinit var _appContext: Context

    val user: Persist by lazy {
        Persist(_appContext.getSharedPreferences(SCOPE_USER, Context.MODE_PRIVATE))
    }
    val app: Persist by lazy {
        Persist(_appContext.getSharedPreferences(SCOPE_APP, Context.MODE_PRIVATE))
    }

    fun init(applicationContext: Context) {
        _appContext = applicationContext
    }

    class Persist(private val _sharedPref: SharedPreferences) {
        fun clearAll() {
            _sharedPref.edit().clear().apply()
        }

        fun contains(key: String): Boolean {
            return _sharedPref.contains(key)
        }

        fun removeKeyValuePair(key: String) {
            _sharedPref.edit().remove(key).apply()
        }

        fun putString(key: String, value: String?) {
            _sharedPref.edit().putString(key, value).apply()
        }

        fun putBoolean(key: String, value: Boolean) {
            _sharedPref.edit().putBoolean(key, value).apply()
        }

        fun putStringSet(key: String, value: Set<String>) {
            _sharedPref.edit().putStringSet(key, value).apply()
        }

        fun getString(key: String, default: String): String {
            return _sharedPref.getString(key, default) ?: default
        }

        fun getStringOrNull(key: String): String? {
            return _sharedPref.getString(key, null)
        }

        fun getBoolean(key: String, default: Boolean): Boolean {
            return _sharedPref.getBoolean(key, default)
        }

        fun getStringSet(key: String, default: Set<String>): Set<String>? {
            return _sharedPref.getStringSet(key, default)
        }
    }


}