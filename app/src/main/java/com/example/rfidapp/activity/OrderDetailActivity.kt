package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.OrderDetailAdapter
import com.example.rfidapp.databinding.ActivityOrderDetailBinding
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.openPdf
import com.example.rfidapp.util.toFormattedDate
import com.example.rfidapp.viewmodel.OrderDetailViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailActivity : ActBase<ActivityOrderDetailBinding>() {

    private val viewModel: OrderDetailViewModel by viewModels()

    override fun setViewBinding() = ActivityOrderDetailBinding.inflate(layoutInflater)

    private var orderDetail: OrderDetail?= null
    override fun bindObjects() {
        intent.getStringExtra("ORDER_ID")?.let {
            viewModel.fetchOrderDetail(it)
        }
    }

    private fun updateOrderDetail(orderDetail: OrderDetail) {
        this.orderDetail = orderDetail
        binding.orderId.text = orderDetail.referenceId
        binding.carrierName.text = orderDetail.carrier?.name
        binding.customerName.text = orderDetail.customer?.name
        binding.pickupDate.text = orderDetail.requiredDate?.toFormattedDate()
        binding.orderDate.text = orderDetail.createdAt?.toFormattedDate()
        if (orderDetail.items.isEmpty().not()) {
            binding.rcvOrders.isVisible = true
            binding.lnrContent.isVisible = true
            initView(orderDetail.items)
        } else {
            binding.rcvOrders.isVisible = false
            binding.lnrContent.isVisible = false
        }
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
                            Log.e("TAG243", "bindListeners: "+  it.response)
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

                toolbarTitle.text = "Order Details"
            }

            footer.apply {
                outlinedOutlined.text = "Print Order Info"
                filledButton.text = "Scan for Shipment"

                filledButton.setOnClickListener {
                    Log.e("TAG243", "bindListeners: orderDetail"+ Gson().toJson(orderDetail))
                    startActivity(
                        Intent(
                            this@OrderDetailActivity,
                            InventoryItemsActivity::class.java
                        ).putExtra("orderDetail", Gson().toJson(orderDetail))
                    )
                    finish()
                }
                outlinedOutlined.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        runOnUiThread{
                            progressBar.isVisible = true
                        }
                        viewModel.fetchOrderPdf().collectLatest {
                            runOnUiThread {
                                progressBar.isVisible = false
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

    private fun initView(items: List<OrderDetail.Item>) {
        val adapter = OrderDetailAdapter(
            orderId = orderDetail?.id ?: "",
            orderType = orderDetail?.type?:"",
            activity = this,
            orderList = items,
            onItemClick = {
                showAddItemQuantityDialog()
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }

    fun showAddItemQuantityDialog() {
        val input = EditText(this).apply {
            hint = "Enter item quantity"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val container = FrameLayout(this).apply {
            addView(input, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
                marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
            })
        }

        // Build the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Item Quantity")
            .setMessage("Please enter the quantity of the item you want to add:")
            .setView(container)
            .setPositiveButton("Add") { dialog, _ ->
                val quantity = input.text.toString()
                if (quantity.isNotEmpty()) {
                    //Data
                } else {
                    showToast("Quantity cannot be empty")
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create()

        dialog.show()
    }


}