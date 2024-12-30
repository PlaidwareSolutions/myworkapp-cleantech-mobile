package com.example.rfidapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.rfidapp.model.network.Contact
import com.example.rfidapp.model.network.LoginResponse
import com.google.gson.Gson

class SharedPrefs {

    private lateinit var context: Context

    private fun getContext() = if (::context.isInitialized) context
    else throw RuntimeException("Please Initialize SharedPrefs")

    private var initialized = false
    private var listeners: ArrayList<SharedPreferences.OnSharedPreferenceChangeListener> =
        arrayListOf()

    private val pref by lazy {
        getInstance().getContext().getSharedPreferences("GPS_Map_Camera_Lite", Context.MODE_PRIVATE)
    }

    private fun edit(operation: SharedPreferences.Editor.() -> Unit) {
        val editor = getInstance().pref.edit()
        operation(editor)
        editor.apply()
    }

    companion object {
        fun init(context: Context) {
            if (getInstance().initialized.not()) {
                getInstance().context = context
                getInstance().initialized = true
            } else println("Already initialized")
        }

        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPrefs? = null

        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val CONTACT = "CONTACT"

        private fun getInstance(): SharedPrefs {
            return instance ?: synchronized(this) { SharedPrefs().also { instance = it } }
        }

        fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            if (!getInstance().listeners.contains(listener)) {
                getInstance().listeners.add(listener)
                getInstance().pref.registerOnSharedPreferenceChangeListener(listener)
            }
        }

        var accessToken: String?
            get() = getInstance().pref.getString(ACCESS_TOKEN, null)
            set(value) = getInstance().edit { putString(ACCESS_TOKEN, value) }

        var contact: String?
            get() = getInstance().pref.getString(CONTACT, null)
            set(value) = getInstance().edit { putString(CONTACT, value) }

        fun getContact(): LoginResponse.Contact? {
            val jsonString = contact
            return if (jsonString != null) {
                Gson().fromJson(jsonString, LoginResponse.Contact::class.java)
            } else {
                null
            }
        }

        fun clearSharedPreferences() {
            getInstance().pref.edit().clear().apply()
        }

    }
}