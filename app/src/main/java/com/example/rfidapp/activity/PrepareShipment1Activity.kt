package com.example.rfidapp.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.ShipmentOrderAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipment1Binding
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.CreateShipmentRequest
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.core.ShipmentUtil
import com.example.rfidapp.util.openPdf
import com.example.rfidapp.viewmodel.ShipmentDetailViewModel
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
    private val shipmentDetailVM: ShipmentDetailViewModel by viewModels()
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.createShipmentList.collectLatest {
                runOnUiThread {
                    when(it){
                        ScreenState.Idle -> {}
                        ScreenState.Loading -> {
                            binding.progressBar.isVisible = true

                        }

                        is ScreenState.Success -> {
                            it.response.let { it1 ->
                                Toast.makeText(this@PrepareShipment1Activity, "Shipment created successfully", Toast.LENGTH_SHORT).show()
                                shipmentDetailVM.fetchShipmentPdf(referenceId = it1.id)
                            }
                        }

                        is ScreenState.Error -> {
                            binding.progressBar.isVisible = false
                            showToast(it.message)
                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            shipmentDetailVM.shipmentPdf.collectLatest {
                runOnUiThread {
                    when (it) {
                        ScreenState.Idle -> {}
                        ScreenState.Loading -> {
                            binding.progressBar.isVisible = true
                        }

                        is ScreenState.Error -> {
                            binding.progressBar.isVisible = false
                            showToast(it.message)
                        }

                        is ScreenState.Success -> {
                            it.response.let {
                                binding.progressBar.isVisible = false
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(
                                        Intent(
                                            this@PrepareShipment1Activity,
                                            HomeScreenActivity::class.java
                                        ).apply {
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        })
                                }, 1500)
                                it?.url?.let { it1 -> openPdf(it1) }
                                /*downloadPDF(
                                        activity = this@PrepareShipment1Activity,
                                        fileUrl =  it.url?:"",
                                        mFileName = it.fileName,
                                        isDownload = true,
                                        onSuccessListener = { s: String ->
                                            runOnUiThread {
                                                showToast(
                                                    ("Your file is ready to view in Downloads folder.")
                                                )
                                                this@PrepareShipment1Activity.openDocument((s))
                                            }
                                        },
                                        onFailureListener = { s: String ->
                                            runOnUiThread {
                                               showToast(
                                                    (s)
                                                )
                                            }
                                        }
                                    )*/
                            }
                        }
                    }
                }
            }
        }

        binding.apply {

            filledButton.setOnClickListener {
                if (!isFinishing && !isDestroyed) {
                    confirmationDialog()
                }
            }

            addOrder.setOnClickListener {
                val intent =
                    Intent(this@PrepareShipment1Activity, PrepareShipmentActivity::class.java)
                intent.putExtra("SHOULD_CLEAR", false)
                intent.putExtra("shipmentType", "orders")
                startActivity(intent)
//                finish()
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
            filledButton.text = "Finalize & Print"
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
        val hasShippedItems = items.any { it.shippedQuantity > 0 }
        binding.filledButton.isVisible = hasShippedItems
    }

    /*private fun updateOrderDetail(orderDetail: OrderDetail) {
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
    }*/

    private fun handleBackNavigation() {
        val resultIntent = Intent().apply {
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    private fun initData(){
        binding.apply {
            createShipmentRequest?.let {
                txtShipmentId.text = ""
                val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val date: Date = originalFormat.parse(it.shipmentDate ?: "") ?: return

                val desiredFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                val formattedDate = desiredFormat.format(date)
                shipmentDate.text = formattedDate
                carrierName.text = it.carrierName ?: ""
                driverName.setText(it.driver?.name ?: "")
            }
        }
    }

    private fun confirmationDialog() {
        if (isFinishing || isDestroyed) return // Prevents leak

        val alertDialog = AlertDialog.Builder(this@PrepareShipment1Activity)
            .setIcon(R.drawable.ic_logo)
            .setTitle("Shipment Confirmation")
            .setMessage("This action confirms the item being added to the order. Once confirmed, this action can not be undone.")
            .setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                createShipmentRequest?.let { request -> viewModel.createShipments(request) }
            }
            .setNegativeButton("No", null)
            .show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.app_color_red))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.rs_green))
    }

}