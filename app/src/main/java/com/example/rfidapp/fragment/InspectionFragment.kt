package com.example.rfidapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rfidapp.R
import com.example.rfidapp.databinding.FragmentInspectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InspectionFragment : BottomSheetDialogFragment(R.layout.fragment_inspection) {

    lateinit var binding: FragmentInspectionBinding
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
        binding = FragmentInspectionBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            addInspection.setOnClickListener {
                val addInspectionFragment = AddInspectionFragment.newInstance(tagID)
                addInspectionFragment.show(
                    childFragmentManager,
                    addInspectionFragment.tag
                )
                dismiss()
            }

            inspectionHistory.setOnClickListener {
                val inspectionHistoryFragment = InspectionHistoryFragment.newInstance(tagID)
                inspectionHistoryFragment.show(
                    childFragmentManager,
                    inspectionHistoryFragment.tag
                )
                dismiss()
            }

            cancel.setOnClickListener {
                dismiss()
            }
        }
    }
}