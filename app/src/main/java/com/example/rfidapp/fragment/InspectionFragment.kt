package com.example.rfidapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rfidapp.R
import com.example.rfidapp.databinding.FragmentAddInspectionBinding
import com.example.rfidapp.util.views.MaxHeightBottomSheet

class InspectionFragment() : MaxHeightBottomSheet(R.layout.fragment_add_inspection) {

    lateinit var binding: FragmentAddInspectionBinding


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
        return binding.root
    }

    private fun setupUI() {
        binding.apply {

        }
    }
}