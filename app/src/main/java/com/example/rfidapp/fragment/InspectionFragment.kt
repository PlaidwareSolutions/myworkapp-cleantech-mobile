package com.example.rfidapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.rfidapp.R
import com.example.rfidapp.databinding.FragmentAddInspectionBinding
import com.example.rfidapp.viewmodel.AssetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InspectionFragment() : BottomSheetDialogFragment(R.layout.fragment_add_inspection) {

    lateinit var binding: FragmentAddInspectionBinding
    private val assetViewModel: AssetViewModel by viewModels()


    companion object {
        @JvmStatic
        fun newInstance() = InspectionFragment().apply {
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

        }
    }

    private fun setupObservers() {

    }
}