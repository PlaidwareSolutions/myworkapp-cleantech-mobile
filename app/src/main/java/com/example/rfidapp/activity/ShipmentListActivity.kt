package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.ShipmentAdapter
import com.example.rfidapp.databinding.ActivityShipmentListBinding
import com.example.rfidapp.model.network.Contact
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.KeyConstants.CUSTOMER_TYPE_CARRIER
import com.example.rfidapp.util.KeyConstants.TAG_BILL_OF_LADING
import com.example.rfidapp.util.KeyConstants.TAG_CARRIER
import com.example.rfidapp.util.KeyConstants.TAG_CUSTOMER
import com.example.rfidapp.util.KeyConstants.TAG_ORDER
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.core.ShipmentUtil
import com.example.rfidapp.util.hideKeyboard
import com.example.rfidapp.viewmodel.OrderViewModel
import com.example.rfidapp.viewmodel.ShipmentViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShipmentListActivity : ActBase<ActivityShipmentListBinding>() {

    private val orderViewModel: OrderViewModel by viewModels()

    private val viewModel: ShipmentViewModel by viewModels()
    private var shipmentType: String = ""

    private var shipmentAdapter: ShipmentAdapter? = null

    override fun setViewBinding() = ActivityShipmentListBinding.inflate(layoutInflater)

    override fun bindObjects() {
        viewModel.getShipments()
        shipmentType = intent.getStringExtra("shipmentType") ?: ""
        orderViewModel.fetchContacts()
    }

    override fun bindListeners() {
        setSearchViewListener()

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.selectedShipment.collectLatest { order ->
                runOnUiThread {
                    binding.receiveShipmentButton.isEnabled = order != null
                }
            }
        }

        binding.receiveShipmentButton.setOnClickListener {
            viewModel.selectedShipment.value?.let {
                if(mReader != null){
                    mReader.setPower(6)
                }
                val intent = Intent(this, InventoryItemsActivity::class.java)
                intent.putExtra("SHIPMENT", Gson().toJson(it))
                intent.putExtra(
                    "maxQuantity",
                    it.bols?.firstOrNull()?.items?.firstOrNull()?.requiredQuantity
                        ?: it.bols?.firstOrNull()?.items?.firstOrNull()?.quantity ?: 10000
                )
                Log.e(
                    "TAG111", "bindListeners: "
                            + (it.bols?.firstOrNull()?.items?.firstOrNull()?.requiredQuantity
                        ?: it.bols?.firstOrNull()?.items?.firstOrNull()?.quantity ?: 1000)
                )
                startActivity(intent)
            }
        }

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shipmentList.collectLatest { state ->
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
                                shipmentAdapter?.updateData(state.response) ?: run {
                                    initAdapter(state.response as ArrayList<Shipment>)
                                }
                            }

                            is ScreenState.Error -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(
                                    this@ShipmentListActivity,
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
                resetValues()
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

        binding.swipeRefresh.setOnRefreshListener {
            resetValues()
            viewModel.getShipments()
            orderViewModel.fetchContacts()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun bindMethods() {
        binding.toolbar.toolbarTitle.text = "Receive Shipment"
        initTabs()
        initAdapter()
        setupSpinner()
    }

    private fun setupSpinner() {
        lifecycleScope.launch {
            orderViewModel.contacts.collectLatest { contacts ->
                runOnUiThread {
                    initAutoCompleteAdapter(contacts)
                    /*val sortedNames = spinnerData.sortedBy { it.name.lowercase() }.map { it.name }
                    val memberAdapter = ACArrayAdapter(
                        this@PrepareShipmentActivity,
                        R.layout.row_spinner,
                        sortedNames
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
                        }*/
                }
            }
        }
    }

    private fun initAutoCompleteAdapter(contacts: ArrayList<Contact>) {

        val spinnerData: ArrayList<Contact> = arrayListOf()
        when (binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.tag) {
            TAG_CUSTOMER -> {
                spinnerData.addAll(contacts)
            }

            TAG_CARRIER -> {
                spinnerData.addAll(contacts.filter { it.type == CUSTOMER_TYPE_CARRIER } as ArrayList<Contact>)
            }

            else -> {
                spinnerData.addAll(contacts)
            }
        }

        val suggestions = spinnerData.sortedBy { it.name.lowercase() }.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        binding.searchAutoComplete.setAdapter(adapter)
        /*binding.searchAutoComplete.onItemSelectedListener =
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
                        orderViewModel.performSearchByCustomer(contacts[position].id)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }*/

        binding.searchAutoComplete.setOnItemClickListener { _, _, _, _ ->
            spinnerData.firstOrNull { it.name == binding.searchAutoComplete.text.toString() }
                ?.let { selectedContact ->
                    binding.searchAutoComplete.setText(selectedContact.name)
                    binding.searchAutoComplete.clearFocus()
                    orderViewModel.performSearchByCustomer(selectedContact.id)
                }
        }

        binding.searchAutoComplete.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                orderViewModel.fetchOrders()
            }
        }
    }

    private fun initAdapter(shipmentList: ArrayList<Shipment> = arrayListOf()) {
        shipmentAdapter = ShipmentAdapter(
            activity = this,
            orderList = shipmentList,
            onItemClick = {
                viewModel.selectedShipment.value = it
                hideKeyboard()
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = shipmentAdapter
    }

    private fun initTabs() {
        binding.apply {
            tabLayout.addTab(
                tabLayout
                    .newTab()
                    .setText(getString(R.string.bill_of_lading_))
                    .setTag(TAG_BILL_OF_LADING)
            )
            tabLayout.addTab(
                tabLayout
                    .newTab()
                    .setText(getString(R.string.carrier))
                    .setTag(TAG_CARRIER)
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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

            searchAutoComplete.addTextChangedListener(object : TextWatcher {
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

                    searchAutoComplete.setCompoundDrawables(
                        searchIcon,
                        null,
                        closeIcon,
                        null
                    )
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

            searchAutoComplete.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = searchAutoComplete.compoundDrawables[2]
                    if (drawableEnd != null) {
                        val drawableWidth = drawableEnd.bounds.width()
                        val touchAreaStart =
                            searchAutoComplete.width - searchAutoComplete.paddingEnd - drawableWidth
                        if (event.rawX >= touchAreaStart) {
                            searchAutoComplete.text.clear()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }

            search.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Perform your search logic here
                    //performSearch(editText.text.toString())
                    hideKeyboard()
                    return@OnEditorActionListener true
                }
                false
            })
        }
    }

    fun resetValues() {
        binding.search.text.clear()
        binding.searchAutoComplete.text.clear()
        binding.search.clearFocus()
        binding.searchAutoComplete.clearFocus()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
//        checkBTConnect()
        if (intent.getBooleanExtra("SHOULD_CLEAR", true)) {
            ShipmentUtil.clearAll()
        }
    }
}