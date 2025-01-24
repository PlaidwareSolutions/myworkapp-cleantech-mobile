package com.example.rfidapp.activity

import android.content.Intent
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityCreateBolBinding
import com.example.rfidapp.util.ActBase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateBoLActivity : ActBase<ActivityCreateBolBinding>() {

    override fun setViewBinding() = ActivityCreateBolBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.bill_of_lading)
                btnBack.setOnClickListener {
                    finish()
                }
            }

            doneButton.setOnClickListener {
                startActivity(Intent(this@CreateBoLActivity, BoLActivity::class.java))
                finish()
            }
        }


    }

    override fun bindMethods() {
    }

}