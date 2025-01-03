package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.ACArrayAdapter
import com.example.rfidapp.adapter.OrderAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipmentBinding
import com.example.rfidapp.model.network.Contact
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ItemMarginDecoration
import com.example.rfidapp.util.KeyConstants.CUSTOMER_TYPE_CARRIER
import com.example.rfidapp.util.KeyConstants.TAG_BILL_OF_LADING
import com.example.rfidapp.util.KeyConstants.TAG_CARRIER
import com.example.rfidapp.util.KeyConstants.TAG_CUSTOMER
import com.example.rfidapp.util.KeyConstants.TAG_ORDER
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.viewmodel.OrderViewModel
import com.example.rfidapp.viewmodel.PrepareShipmentViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrepareShipmentActivity : ActBase<ActivityPrepareShipmentBinding>() {

    private val orderViewModel: OrderViewModel by viewModels()

    private val viewModel: PrepareShipmentViewModel by viewModels()
    private var shipmentType: String = ""

    private var orderAdapter: OrderAdapter? = null

    override fun setViewBinding() = ActivityPrepareShipmentBinding.inflate(layoutInflater)

    override fun bindObjects() {
        shipmentType = intent.getStringExtra("shipmentType") ?: ""
        orderViewModel.fetchContacts()
    }

    override fun bindListeners() {
        setSearchViewListener()

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.selectedOrder.collectLatest { order ->
                runOnUiThread {
                    binding.orderDetailsButton.isEnabled = order != null
                }
            }
        }

        binding.orderDetailsButton.setOnClickListener {
            viewModel.selectedOrder.value?.id?.let {
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", it)
                startActivity(intent)
            }
        }

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.orderList.collectLatest { state ->
                    runOnUiThread {
                        when (state) {
                            is ScreenState.Idle -> {}
                            is ScreenState.Loading -> {
                                binding.progressBar.isVisible = true
                            }

                            is ScreenState.Success -> {
                                binding.progressBar.isVisible = false
                                binding.noDataLayout.isVisible = state.response.isEmpty()
                                binding.noData.noDataText.text = getString(R.string.no_order_found)
                                orderAdapter?.updateData(state.response) ?: run {
                                    initAdapter(state.response as ArrayList<Order>)
                                }
                            }

                            is ScreenState.Error -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(
                                    this@PrepareShipmentActivity,
                                    state.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.searchAutoComplete.isVisible =
                    (tab?.tag == TAG_CUSTOMER || tab?.tag == TAG_CARRIER)
                binding.search.isVisible = (tab?.tag != TAG_CUSTOMER && tab?.tag != TAG_CARRIER)
                setupSpinner()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
//        binding.searchAutoComplete.onItemSelectedListener { adapterView, view, position, l ->
//            val selectedContact = contactList[position]
////            binding.searchAutoComplete.setText(selectedContact.name)
//            binding.searchAutoComplete.clearFocus()
//            orderViewModel.performSearchByCustomer(selectedContact.id)
//        }
//        binding.searchAutoComplete.setOnItemClickListener { _, _, _, _ ->
//            contactList.firstOrNull { it.name == binding.searchAutoComplete.text.toString() }
//                ?.let { selectedContact ->
//                    binding.searchAutoComplete.setText(selectedContact.name)
//                    binding.searchAutoComplete.clearFocus()
//                    orderViewModel.performSearchByCustomer(selectedContact.id)
//                }
//        }
    }

    override fun bindMethods() {
        initTabs()
        initAdapter()
        setupSpinner()
    }

    private fun setupSpinner() {
        lifecycleScope.launch {
            orderViewModel.contacts.collectLatest { contacts ->
                runOnUiThread {
                    val spinnerData: ArrayList<Contact> = arrayListOf()
                    when (binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.tag) {
                        TAG_CUSTOMER -> {
                            spinnerData.add(0, Contact(id = "-1", name = "All Customers"))
                            spinnerData.addAll(contacts)
                        }
                        TAG_CARRIER -> {
                            spinnerData.add(0, Contact(id = "-1", name = "All Carriers"))
                            spinnerData.addAll(contacts.filter { it.type == CUSTOMER_TYPE_CARRIER } as ArrayList<Contact>)
                        }

                        else -> {
                            spinnerData.add(0, Contact(id = "-1", name = "All"))
                            spinnerData.addAll(contacts)
                        }
                    }
                    val memberAdapter = ACArrayAdapter(
                        this@PrepareShipmentActivity,
                        R.layout.row_spinner,
                        spinnerData.map { it.name }
                    )

                    memberAdapter.setDropDownViewResource(R.layout.row_drop_down_spinner)
                    binding.searchAutoComplete.adapter = memberAdapter
                    binding.searchAutoComplete.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position == 0) {
                                    orderViewModel.fetchOrders()
                                    return
                                }
                                val selectedContact = spinnerData[position]
                                orderViewModel.performSearchByCustomer(selectedContact.id)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            }
        }
    }

    private fun initAutoCompleteAdapter(contactList: ArrayList<Contact>) {
        contactList.add(0, Contact(id = "", name = "All Customers"))
        binding.searchAutoComplete.adapter = null
        val suggestions = contactList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        binding.searchAutoComplete.adapter = adapter
        binding.searchAutoComplete.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position) as String
                    if (selectedItem == "All") {
                        orderViewModel.fetchOrders()
                    } else {
                        orderViewModel.performSearchByCustomer(contactList[position].id)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun initAdapter(orderList: ArrayList<Order> = arrayListOf()) {
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_margin)
        binding.rcvOrders.addItemDecoration(ItemMarginDecoration(marginInPixels))
        orderAdapter = OrderAdapter(
            activity = this,
            orderList = orderList,
            onItemClick = {
                viewModel.selectedOrder.value = it
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = orderAdapter
    }

    private fun initTabs() {
        binding.apply {
            when (shipmentType) {
                "shipping" -> {
                    tabLayout.addTab(
                        tabLayout
                            .newTab()
                            .setText(getString(R.string.order))
                            .setTag(TAG_ORDER)
                    )
                    tabLayout.addTab(
                        tabLayout
                            .newTab()
                            .setText(getString(R.string.customer)).setTag(TAG_CUSTOMER)
                    )
                    tabLayout.addTab(
                        tabLayout
                            .newTab()
                            .setText(getString(R.string.carrier))
                            .setTag(TAG_CARRIER)
                    )
                    toolbar.toolbarTitle.text = getString(R.string.prepare_shipment)

                }

                "receiving" -> {
                    tabLayout.addTab(
                        tabLayout.newTab().setText(getString(R.string.bill_of_lading_)).setTag(
                            TAG_BILL_OF_LADING
                        )
                    )
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.carrier)).setTag(
                            TAG_CARRIER
                        )
                    )
                    toolbar.toolbarTitle.text = getString(R.string.receive_shipment)

                }

                "orders" -> {
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.order))
                            .setTag(TAG_ORDER)
                    )
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.customer)).setTag(
                            TAG_CUSTOMER
                        )
                    )
                    toolbar.toolbarTitle.text = getString(R.string.order_search)
                }

                "lookup" -> {
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.bill_of_lading_))
                            .setTag(
                                TAG_BILL_OF_LADING
                            )
                    )
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.carrier)).setTag(
                            TAG_CARRIER
                        )
                    )
                    toolbar.toolbarTitle.text = getString(R.string.receive_shipment)
                }

                else -> {
                }
            }
        }
    }

    private fun setSearchViewListener() {
        binding.apply {
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val closeIcon: Drawable? =
                        if (!s.isNullOrEmpty()) getDrawable(R.drawable.ic_close) else null
                    closeIcon?.setBounds(0, 0, closeIcon.intrinsicWidth, closeIcon.intrinsicHeight)

                    val searchIcon: Drawable? = getDrawable(R.drawable.ic_search)
                    searchIcon?.setBounds(
                        0,
                        0,
                        searchIcon.intrinsicWidth,
                        searchIcon.intrinsicHeight
                    )

                    search.setCompoundDrawables(
                        searchIcon,
                        null,
                        closeIcon,
                        null
                    )
                    when (binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.tag) {
                        TAG_ORDER -> {
                            orderViewModel.performSearchByReferenceId(s.toString())
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            search.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = search.compoundDrawables[2]
                    if (drawableEnd != null) {
                        val drawableWidth = drawableEnd.bounds.width()
                        val touchAreaStart = search.width - search.paddingEnd - drawableWidth
                        if (event.rawX >= touchAreaStart) {
                            search.text.clear()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
    }
}