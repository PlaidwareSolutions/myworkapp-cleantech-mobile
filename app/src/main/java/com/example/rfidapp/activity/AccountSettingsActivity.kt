package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
                logoutDialog()
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

    private fun logoutDialog() {
        val alertDialog =  AlertDialog.Builder(this).setIcon(R.drawable.ic_logo)
            .setTitle("Logout Confirmation" as CharSequence)
            .setMessage("Are you sure you want to log out? Your current session will end, and you will need to log in again to access your account." as CharSequence).setPositiveButton(
                "Yes" as CharSequence
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                clearSharedPreferences()
                val intent = Intent(this@AccountSettingsActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .setNegativeButton("No" as CharSequence, null as DialogInterface.OnClickListener?)
            .show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.app_color_red))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.rs_green))

    }
}