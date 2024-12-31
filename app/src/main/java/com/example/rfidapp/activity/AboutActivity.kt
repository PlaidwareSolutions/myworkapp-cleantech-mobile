package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.provider.Settings
import com.example.rfidapp.BuildConfig
import com.example.rfidapp.databinding.ActivityAboutBinding
import com.example.rfidapp.util.ActBase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AboutActivity : ActBase<ActivityAboutBinding>() {

    override fun setViewBinding() = ActivityAboutBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = "About Application"

                btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    override fun bindMethods() {
        binding.apply {
            buildVersion.text = BuildConfig.VERSION_NAME
            buildType.text = BuildConfig.BUILD_TYPE
            val buildTimeMillis = BuildConfig.BUILD_TIME.toLong()
            val buildTimeTxt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(buildTimeMillis))
            buildTime.text = buildTimeTxt
            remoteApi.text =  BuildConfig.BASE_URL
            hardwareId.text = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        }
    }
}