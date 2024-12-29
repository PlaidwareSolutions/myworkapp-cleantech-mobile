package com.example.rfidapp.activity

import android.content.Intent
import android.os.Looper
import com.example.rfidapp.databinding.ActivitySplashBinding
import com.example.rfidapp.util.ActBase

class SplashActivity : ActBase<ActivitySplashBinding>() {

    override fun setViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }, 3000)
        }
    }

    override fun bindMethods() {

    }
}