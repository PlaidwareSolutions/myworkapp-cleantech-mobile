package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.rfidapp.databinding.ActivityHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                btnBack.isVisible = false
                toolbarTitle.text = "MyWorkApp"
                toolbar.txtMyWorkApp.visibility = View.INVISIBLE
            }

            orders.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, PrepareShipmentActivity::class.java).putExtra("shipmentType", "orders"))
            }

            billOfLading.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, BoLActivity::class.java))
            }
            iconImage.setOnClickListener {
                scanningToggleVisibility()
            }

            beginScanning.setOnClickListener {
                scanningToggleVisibility()
            }

            shipping.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, PrepareShipmentActivity::class.java).putExtra("shipmentType", "shipping"))
            }

            receiving.setOnClickListener {
                startActivity(
                    Intent(
                        this@HomeScreenActivity,
                        ShipmentListActivity::class.java
                    )
                )
            }

            inventory.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, InventoryActivity::class.java))
            }
            settings.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, SettingsActivity::class.java))
            }
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