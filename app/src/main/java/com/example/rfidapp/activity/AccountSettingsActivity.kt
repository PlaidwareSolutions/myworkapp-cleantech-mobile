package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityAccountBinding
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.SharedPrefs
import com.example.rfidapp.util.SharedPrefs.Companion.clearSharedPreferences


class AccountSettingsActivity : ActBase<ActivityAccountBinding>() {

    override fun setViewBinding() = ActivityAccountBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.account_settings)

                btnBack.setOnClickListener {
                    finish()
                }
            }

            binding.logout.setOnClickListener {
                clearSharedPreferences()
                val intent =
                    Intent(this@AccountSettingsActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("HardwareIds")
    override fun bindMethods() {
        binding.apply {
            val contact = SharedPrefs.getContact()
            contact?.apply {
                userDisplayName.text = systemUser?.username
                userName.text = systemUser?.username
                role.text = type?.name
                accountName.text = name
                userAccountNumber.text = "1"
            }
        }
    }
}