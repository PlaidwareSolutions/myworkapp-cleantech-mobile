package com.example.rfidapp.activity

import android.content.Intent
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityCreateBolBinding
import com.example.rfidapp.model.network.Contact
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateBoLActivity : ActBase<ActivityCreateBolBinding>() {

    private var selectedCustomer: String? = null
    private var selectedCarrier: String? = null

    private val viewModel: OrderViewModel by viewModels()

    override fun setViewBinding() = ActivityCreateBolBinding.inflate(layoutInflater)

    override fun bindObjects() {
        viewModel.fetchContacts()
    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.bill_of_lading)
                btnBack.setOnClickListener { finish() }
            }

            doneButton.setOnClickListener {
                val intent = Intent(this@CreateBoLActivity, BoLActivity::class.java)
                if (binding.bolNumber.text.trim().isNotEmpty()) {
                    intent.putExtra("bolNumber", binding.bolNumber.text.trim().toString())
                }
                if (binding.shipmentNumber.text.trim().isNotEmpty()) {
                    intent.putExtra("shipmentNumber", binding.shipmentNumber.text.trim().toString())
                }
                if (binding.orderNumber.text.trim().isNotEmpty()) {
                    intent.putExtra("orderNumber", binding.orderNumber.text.trim().toString())
                }
                selectedCustomer?.let {
                    intent.putExtra("customer", it)
                }
                selectedCarrier?.let {
                    intent.putExtra("carrier", it)
                }
                startActivity(intent)
            }
        }


    }

    override fun bindMethods() {
        setupSpinner()
    }

    private fun setupSpinner() {
        lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts ->
                runOnUiThread {
                    initAutoCompleteCustomerAdapter(contacts)
                }
            }
        }
    }

    private fun initAutoCompleteCustomerAdapter(contacts: ArrayList<Contact>) {
        val spinnerData: ArrayList<Contact> = arrayListOf()
        spinnerData.addAll(contacts)
        val suggestions = spinnerData.sortedBy { it.name.lowercase() }.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        binding.searchAutoCompleteCustomer.setAdapter(adapter)

        binding.searchAutoCompleteCustomer.setOnItemClickListener { _, _, _, _ ->
            spinnerData.firstOrNull { it.name == binding.searchAutoCompleteCustomer.text.toString() }
                ?.let { selectedContact ->
                    binding.searchAutoCompleteCustomer.setText(selectedContact.name)
                    binding.searchAutoCompleteCustomer.clearFocus()
                    selectedCustomer = selectedContact.id
                }
        }
    }

    private fun initAutoCompleteCarrierAdapter(contacts: ArrayList<Contact>) {
        val spinnerData: ArrayList<Contact> = arrayListOf()
        spinnerData.addAll(contacts)
        val suggestions = spinnerData.sortedBy { it.name.lowercase() }.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        binding.searchAutoCompleteCarrier.setAdapter(adapter)

        binding.searchAutoCompleteCarrier.setOnItemClickListener { _, _, _, _ ->
            spinnerData.firstOrNull { it.name == binding.searchAutoCompleteCarrier.text.toString() }
                ?.let { selectedContact ->
                    binding.searchAutoCompleteCarrier.setText(selectedContact.name)
                    binding.searchAutoCompleteCarrier.clearFocus()
                    selectedCarrier = selectedContact.id
                }
        }
    }

}