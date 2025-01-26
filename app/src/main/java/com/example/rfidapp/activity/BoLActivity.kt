package com.example.rfidapp.activity

import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.BolAdapter
import com.example.rfidapp.databinding.ActivityBolBinding
import com.example.rfidapp.model.network.Bol
import com.example.rfidapp.model.network.BolX
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.viewmodel.BolViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoLActivity : ActBase<ActivityBolBinding>() {

    private val bolViewModel: BolViewModel by viewModels()

    override fun setViewBinding() = ActivityBolBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.bill_of_lading)
                btnBack.setOnClickListener {
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bolViewModel.getBol.collectLatest {
                    runOnUiThread {
                        when (it) {
                            is ScreenState.Idle -> {

                            }

                            is ScreenState.Loading -> {
                                binding.progressBar.isVisible = true
                            }

                            is ScreenState.Success -> {
                                binding.progressBar.isVisible = false
                                it.response.let {
                                    if (it.isNotEmpty()) {
                                        setUpAdapter(it)
                                    } else {
                                        binding.rcvBol.isVisible = false
                                        binding.noItem.root.isVisible = true
                                        binding.noItem.noDataText.text = "No Bols Found"
                                    }
                                }
                            }

                            is ScreenState.Error -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(
                                    this@BoLActivity,
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }

    }

    override fun bindMethods() {
        bolViewModel.fetchBolList(
            orderNumber = intent.getStringExtra("orderNumber"),
            customer = intent.getStringExtra("customer"),
            carrier = intent.getStringExtra("carrier"),
            bolNumber = intent.getStringExtra("bolNumber"),
            shipmentNumber = intent.getStringExtra("shipmentNumber")
        )
    }

    private fun setUpAdapter(bolList: List<BolX>) {
        binding.rcvBol.isVisible = true
        val adapter = BolAdapter(
            activity = this,
            bolList = bolList,
            onItemClick = {

            }
        )
        binding.rcvBol.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvBol.adapter = adapter
    }

}