package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.example.rfidapp.BuildConfig
import com.example.rfidapp.R
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
                toolbarTitle.text = getString(R.string.about_application)

                btnBack.setOnClickListener {
                    finish()
                }
            }

            contactSupport.setOnClickListener {
               val url= "https://google.com/"
                url.openLinkInBrowser()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("HardwareIds")
    override fun bindMethods() {
        binding.apply {
            buildVersion.text = "1.0"
            buildType.text = BuildConfig.BUILD_TYPE
            val buildTimeMillis = BuildConfig.BUILD_TIME.toLong()
            val buildTimeTxt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(buildTimeMillis))
            buildTime.text = buildTimeTxt
            remoteApi.text =  BuildConfig.BASE_URL


            val softwareId: String =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Settings.Secure.getString(
                        contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                } else {
                    val manager= getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                    manager.enrollmentSpecificId
                }

            hardwareId.text = softwareId
        }
    }


    private fun String.openLinkInBrowser() {
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(this))
        startActivity(i)
    }
}