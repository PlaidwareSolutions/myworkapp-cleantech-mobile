package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.OrderDetailAdapter
import com.example.rfidapp.databinding.ActivityOrderDetailBinding
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.toFormattedDate
import com.example.rfidapp.viewmodel.OrderDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailActivity : ActBase<ActivityOrderDetailBinding>() {

    private val viewModel: OrderDetailViewModel by viewModels()

    override fun setViewBinding() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {
        intent.getStringExtra("ORDER_ID")?.let {
            viewModel.fetchOrderDetail(it)
        }
    }

    private fun updateOrderDetail(orderDetail: OrderDetail) {
        binding.orderId.text = orderDetail.id
        binding.carrierName.text = orderDetail.carrier?.name
        binding.customerName.text = orderDetail.customer?.name
        binding.pickupDate.text = orderDetail.requiredDate?.toFormattedDate()
        binding.orderDate.text = orderDetail.createdAt?.toFormattedDate()
        initView(orderDetail)
    }

    @SuppressLint("SetTextI18n")
    override fun bindListeners() {

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.orderDetail.collectLatest {
                runOnUiThread {
                    when(it){
                        ScreenState.Idle -> {}
                        ScreenState.Loading -> {
                            binding.progressBar.isVisible = true

                        }

                        is ScreenState.Success -> {
                            binding.progressBar.isVisible = false
                            it.response?.let { it1 -> updateOrderDetail(it1) }
                        }

                        is ScreenState.Error -> {
                            binding.progressBar.isVisible = false
                            showToast(it.message)
                        }
                    }
                }
            }
        }

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
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.fetchOrderPdf().collectLatest {
                            runOnUiThread {
                                it.data?.url?.let { it1 -> openPdf(it1) }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun bindMethods() {
    }

    private fun initView(orderDetail: OrderDetail) {
        val adapter = OrderDetailAdapter(
            activity = this,
            orderList = orderDetail.items,
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }

    private fun openPdf(pdfUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }
}