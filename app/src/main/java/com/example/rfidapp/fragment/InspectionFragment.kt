package com.example.rfidapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.rfidapp.R
import com.example.rfidapp.databinding.FragmentAddInspectionBinding
import com.example.rfidapp.model.network.AssetInspectionRequest
import com.example.rfidapp.model.network.Inspection
import com.example.rfidapp.model.network.UserLocation
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.viewmodel.AssetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InspectionFragment : BottomSheetDialogFragment(R.layout.fragment_add_inspection) {

    lateinit var binding: FragmentAddInspectionBinding
    private val assetViewModel: AssetViewModel by viewModels()
    private var state: String = "GOOD"
    var tagID: String = ""

    companion object {
        @JvmStatic
        fun newInstance(
            tagID: String,
        ) = InspectionFragment().apply {
            this.tagID = tagID
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddInspectionBinding.inflate(inflater, container, false)
        setupUI()
        setupObservers()
        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            editTextAlbumDesc.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isEmpty().not()) {
                    save.isEnabled = true
                    save.isClickable = true
                } else {
                    save.isEnabled = false
                    save.isClickable = false
                }
            }

            radioGroupObservationType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.good -> {
                        state = "CLEANED"
                    }
                    R.id.damaged -> {
                        state = "DAMAGED"
                    }
                    R.id.repaired -> {
                        state = "FIXED"
                    }
                    R.id.scraped -> {
                        state = "DECOMMISSIONED"
                    }
                }
            }

            save.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val listInspection = arrayListOf(
                        Inspection(
                            comment = editTextAlbumDesc.text.toString(),
                            state = state,
                            tag = tagID
                        )
                    )
                    val request = AssetInspectionRequest(
                        inspection = listInspection,
                        userLocation = UserLocation()
                    )
                    assetViewModel.assetInspection(
                        request = request
                    )
                }

            }

        }
    }

    private fun setupObservers() {
        CoroutineScope(Dispatchers.IO).launch {
            assetViewModel.assetInspection.collectLatest {
                requireActivity().runOnUiThread {
                    when (it) {
                        is ScreenState.Idle -> {

                        }
                        is ScreenState.Loading -> {
                            binding.progressBar.isVisible = true
                        }

                        is ScreenState.Success -> {
                            binding.progressBar.isVisible = false
                            it.response?.let { it->
                                Toast.makeText(
                                    requireActivity(),
                                    "Item inspected successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()
                            }
                        }

                        is ScreenState.Error -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(
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
}