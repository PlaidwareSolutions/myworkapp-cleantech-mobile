package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.OrderDetailAdapter
import com.example.rfidapp.databinding.ActivityOrderDetailBinding
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class OrderDetailActivity : ActBase<ActivityOrderDetailBinding>() {

    private var order: MutableStateFlow<Order?> = MutableStateFlow(null)

    override fun setViewBinding() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {
        order.value = intent.getSerializableExtra("ORDER") as? Order
    }

    @SuppressLint("SetTextI18n")
    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                btnBack.setOnClickListener {
                    finish()
                }
            }

            footer.apply {
                outlinedOutlined.text = "Print Order Info"
                filledButton.text = "Scan for Shipment"

                filledButton.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OrderDetailActivity,
                            InventoryItemsActivity::class.java
                        )
                    )
                }
                outlinedOutlined.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OrderDetailActivity,
                            InventoryItemsActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun bindMethods() {
        initView()
        binding.orderId.text = order.value?.id
        binding.carrierName.text = order.value?.carrier
        binding.customerName.text = order.value?.customer
        binding.pickupDate.text = order.value?.requiredDate?.toFormattedDate() ?: ""
    }

    private fun initView() {
        val adapter = OrderDetailAdapter(
            activity = this,
            orderList = order.value?.items ?: listOf(),
        )
        binding.rcvOrders.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }
}