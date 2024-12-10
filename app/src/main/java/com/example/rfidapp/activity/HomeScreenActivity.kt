package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.rfidapp.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        binding.apply {
            toolbar.apply {
                imageButtonBack.isVisible = false
                textViewTitle.text = "MY Work App"
            }

            orders.setOnClickListener {}
            billOfLading.setOnClickListener {}
            iconImage.setOnClickListener {}
            iconImage.setOnClickListener {
                scanningToggleVisibility()
            }

            beginScanning.setOnClickListener {
                scanningToggleVisibility()
            }

            shipping.setOnClickListener {}
            receiving.setOnClickListener {}
            inventory.setOnClickListener {}
            settings.setOnClickListener {}
        }
    }

    private fun scanningToggleVisibility() {
        binding.apply {
            if (beginScanning.isVisible) {
                beginScanning.isVisible = false
                shippingReceiving.isVisible = true
            } else {
                shippingReceiving.isVisible = false
                beginScanning.isVisible = true
            }
        }
    }
}