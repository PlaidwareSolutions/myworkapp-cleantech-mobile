package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.OrderDetailAdapter
import com.example.rfidapp.databinding.ActivityOrderDetailBinding
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.toFormattedDate
import com.example.rfidapp.viewmodel.OrderDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailActivity : ActBase<ActivityOrderDetailBinding>() {

    private val viewModel: OrderDetailViewModel by viewModels()

    override fun setViewBinding() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {
        intent.getStringExtra("ORDER_ID")?.let {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.orderDetail.collectLatest {
                    updateOrderDetail(it)
                }
            }
        }
    }

    private fun updateOrderDetail(orderDetail: OrderDetail) {
        binding.orderId.text = orderDetail.id
        binding.carrierName.text = orderDetail.carrier
        binding.customerName.text = orderDetail.customer
        binding.pickupDate.text = orderDetail.requiredDate.toFormattedDate()
        initView()
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
        binding.orderId.text = orderDetail.value?.id
        binding.carrierName.text = orderDetail.value?.carrier
        binding.customerName.text = orderDetail.value?.customer
        binding.pickupDate.text = orderDetail.value?.requiredDate?.toFormattedDate() ?: ""
    }

    private fun initView() {
        val adapter = OrderDetailAdapter(
            activity = this,
            orderList = orderDetail.value?.items ?: listOf(),
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }
}