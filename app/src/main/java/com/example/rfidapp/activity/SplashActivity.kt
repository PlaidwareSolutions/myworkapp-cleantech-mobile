package com.example.rfidapp.activity

import android.content.Intent
import android.os.Looper
import com.example.rfidapp.databinding.ActivitySplashBinding
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.SharedPrefs

class SplashActivity : ActBase<ActivitySplashBinding>() {

    override fun setViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                if (SharedPrefs.accessToken == null) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, HomeScreenActivity::class.java))
                }
                finish()
            }, 2000)
        }
    }

    override fun bindMethods() {

    }
}