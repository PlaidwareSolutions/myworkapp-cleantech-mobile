package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.ShipmentOrderAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipment1Binding
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.CreateShipmentRequest
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.core.ShipmentUtil
import com.example.rfidapp.viewmodel.ShipmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PrepareShipment1Activity : ActBase<ActivityPrepareShipment1Binding>() {

    private val viewModel: ShipmentViewModel by viewModels()
    private var createShipmentRequest: CreateShipmentRequest? = null

    override fun setViewBinding() = ActivityPrepareShipment1Binding.inflate(layoutInflater)

    override fun bindObjects() {
    }

    override fun bindListeners() {
        CoroutineScope(Dispatchers.IO).launch {
            ShipmentUtil.createShipment.collectLatest {
                createShipmentRequest = it
                initData()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            ShipmentUtil.orderShipments.collectLatest {
                initViewData(it)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.createShipmentList.collectLatest { state ->
                runOnUiThread {
                    when (state) {
                        is ScreenState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ScreenState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val response = state.response
                            binding.txtShipmentId.text = response.referenceId
                            Toast.makeText(this@PrepareShipment1Activity, "Shipment created successfully", Toast.LENGTH_SHORT).show()
                        }
                        is ScreenState.Error -> {
                            val errorMessage = state.message
                            Toast.makeText(this@PrepareShipment1Activity, errorMessage, Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                        }

                        ScreenState.Idle -> {

                        }
                    }
                }
            }
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })

        binding.apply {
            footer.apply {
                outlinedOutlined.setOnClickListener {
                    createShipmentRequest?.let { it1 -> viewModel.createShipments(it1) }
                }
                filledButton.setOnClickListener {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        runOnUiThread{
//                            progressBar.isVisible = true
//                        }
//                        viewModel.fetchOrderPdf().collectLatest {
//                            runOnUiThread {
//                                progressBar.isVisible = false
//                                it.data?.url?.let { it1 -> openPdf(it1) }
//                            }
//                        }
//                    }
                }
            }

            addOrder.setOnClickListener {
                val intent =
                    Intent(this@PrepareShipment1Activity, PrepareShipmentActivity::class.java)
                intent.putExtra("SHOULD_CLEAR", false)
                intent.putExtra("shipmentType", "orders")
                startActivity(intent)
                finish()
            }
        }
    }

    override fun bindMethods() {
        initView()
    }

    private fun initToolbar() {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                handleBackNavigation()
            }

            toolbar.toolbarTitle.text = "Prepare Shipment"
        }
    }

    fun initView(){
        initToolbar()
        binding.apply {
            footer.apply {
                filledButton.text = "Save Shipment"
                outlinedOutlined.text = "Finalize & Print"
            }
        }
    }

    private fun initViewData(items: List<OrderShipmentData>) {
        val adapter = ShipmentOrderAdapter(
            activity = this,
            orderList = items,
            onItemClick = { orderShipmentData ->
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", orderShipmentData.orderId)
                startActivity(intent)
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }

    private fun handleBackNavigation() {
        val resultIntent = Intent().apply {
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    @SuppressLint("SetTextI18n")
    private fun initData(){
        binding.apply {
            createShipmentRequest?.let {
                txtShipmentId.text = "To be generated"
                val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val date: Date = originalFormat.parse(it.shipmentDate ?: "") ?: return
                val desiredFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                val formattedDate = desiredFormat.format(date)
                shipmentDate.text = formattedDate
                carrierName.text = it.carrier ?: ""
                driverName.setText(it.driver?.name ?: "")
            }
        }
    }

}