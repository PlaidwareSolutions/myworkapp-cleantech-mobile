package com.example.rfidapp.activity

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.ShipmentAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipment1Binding
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrepareShipment1Activity : ActBase<ActivityPrepareShipment1Binding>() {

    override fun setViewBinding() = ActivityPrepareShipment1Binding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {

    }

    override fun bindMethods() {
        initView()
    }

    private fun initToolbar() {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                finish()
            }

            toolbar.toolbarTitle.text = "Prepare Shipment"
        }
    }

    fun initView(){
        initToolbar()
        binding.apply {
            footer.apply {
                outlinedOutlined.text = "Save Shipment"
                filledButton.text = "Finalize & Print"

                filledButton.setOnClickListener {

                }
                outlinedOutlined.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
//                        runOnUiThread{
//                            progressBar.isVisible = true
//                        }
//                        viewModel.fetchOrderPdf().collectLatest {
//                            runOnUiThread {
//                                progressBar.isVisible = false
//                                it.data?.url?.let { it1 -> openPdf(it1) }
//                            }
//                        }
                    }
                }
            }
        }
    }

    private fun initViewData(items: List<OrderDetail.Item>) {
        val adapter = ShipmentAdapter(
            activity = this,
            orderList = items,
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }

    private fun updateOrderDetail(orderDetail: OrderDetail) {
        binding.orderId.text = orderDetail.referenceId
        binding.carrierName.text = orderDetail.carrier?.name
        binding.orderDate.text = orderDetail.createdAt?.toFormattedDate()
        if (orderDetail.items.isEmpty().not()) {
            binding.rcvOrders.isVisible = true
            binding.lnrContent.isVisible = true
            initViewData(orderDetail.items)
        } else {
            binding.rcvOrders.isVisible = false
            binding.lnrContent.isVisible = false
        }
    }

}