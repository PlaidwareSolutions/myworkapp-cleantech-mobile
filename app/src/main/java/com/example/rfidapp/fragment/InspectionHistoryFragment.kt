package com.example.rfidapp.fragment

import android.content.DialogInterface
import android.graphics.Insets.add
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.TagHistoryAdapter
import com.example.rfidapp.databinding.FragmentInspectionHistoryBinding
import com.example.rfidapp.model.network.HistoryAsset
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.views.MaxHeightBottomSheet
import com.example.rfidapp.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray

@AndroidEntryPoint
class InspectionHistoryFragment(var onDismissListner:()->Unit) : MaxHeightBottomSheet(R.layout.fragment_inspection_history) {

    lateinit var binding: FragmentInspectionHistoryBinding
    private val assetViewModel: AssetViewModel by viewModels()
    var tagID: String = ""

    companion object {
        @JvmStatic
        fun newInstance(
            tagID: String,
            onDismissListner:()->Unit
        ) = InspectionHistoryFragment(onDismissListner).apply {
            this.tagID = tagID
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInspectionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        setupUI()
        setUpObserver()
    }

    private fun setUpObserver() {
        val jsonArray = com.google.gson.JsonArray().apply {
            add(tagID)
        }
        assetViewModel.getAssetByTagID(jsonArray)

        CoroutineScope(Dispatchers.IO).launch {
            assetViewModel.assetHistory.collectLatest {
                requireActivity().runOnUiThread {
                    when (it) {
                        is ScreenState.Idle -> {

                        }
                        is ScreenState.Loading -> {
                            binding.progressBar.isVisible = true
                        }

                        is ScreenState.Success -> {
                            binding.progressBar.isVisible = false
                            it.response?.let {
                                if((it[0].history?.size ?: 0) > 0){
                                    setUpAdapter(it[0].product?.name,it[0].history)
                                }else{
                                    binding.rcvHistory.isVisible = false
                                    binding.noItem.root.isVisible = true
                                    binding.noItem.noDataText.text = "No History"
                                }
                            }
                        }

                        is ScreenState.Error -> {
                            binding.progressBar.isVisible = false
                            if(isAdded) Toast.makeText(
                                requireActivity(),
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {

            cancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setUpAdapter(name: String?, history: List<HistoryAsset>?) {
        binding.rcvHistory.isVisible = true
        val adapter = TagHistoryAdapter(
            activity = requireActivity(),
            historyList = history?: arrayListOf(),
            name = name?:"",
            onItemClick = {

            }
        )
        binding.rcvHistory.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcvHistory.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListner.invoke()
        super.onDismiss(dialog)
    }
}