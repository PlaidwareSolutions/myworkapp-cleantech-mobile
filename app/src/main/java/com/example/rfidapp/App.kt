package com.example.rfidapp

import android.app.Application
import com.example.rfidapp.util.SharedPrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefs.init(this)
    }
}