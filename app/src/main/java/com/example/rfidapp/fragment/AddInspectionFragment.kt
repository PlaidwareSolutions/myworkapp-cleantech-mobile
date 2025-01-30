package com.example.rfidapp.fragment

import android.content.DialogInterface
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
class AddInspectionFragment(var onDismissListner:(Boolean)->Unit) : BottomSheetDialogFragment(R.layout.fragment_add_inspection) {

    lateinit var binding: FragmentAddInspectionBinding
    private val assetViewModel: AssetViewModel by viewModels()
    private var state: String = "GOOD"
    var tagID: String = ""
    var isInspected:Boolean = false
    companion object {
        @JvmStatic
        fun newInstance(
            tagID: String,
            onDismissListner:(Boolean)->Unit
        ) = AddInspectionFragment(onDismissListner).apply {
            this.tagID = tagID
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddInspectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.apply {
            editTextAlbumDesc.doOnTextChanged { text, _, _, _ ->
                checkValidation()
            }

            good.setOnClickListener {
                resetRadioButtons()
                good.isChecked = true
                state = "CLEANED"
                editTextAlbumDesc.isEnabled = false
                checkValidation()
            }

            damaged.setOnClickListener {
                resetRadioButtons()
                damaged.isChecked = true
                state = "DAMAGED"
                editTextAlbumDesc.isEnabled = true
                checkValidation()
            }

            repaired.setOnClickListener {
                resetRadioButtons()
                repaired.isChecked = true
                state = "FIXED"
                editTextAlbumDesc.isEnabled = true
                checkValidation()
            }

            scraped.setOnClickListener {
                resetRadioButtons()
                scraped.isChecked = true
                state = "DECOMMISSIONED"
                editTextAlbumDesc.isEnabled = true
                checkValidation()
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

            inspectionHistory.setOnClickListener{
                val inspectionHistoryFragment = InspectionHistoryFragment.newInstance(tagID,{

                })
                inspectionHistoryFragment.show(
                    childFragmentManager,
                    inspectionHistoryFragment.tag
                )
            }

        }
    }

    private fun resetRadioButtons() {
        binding.apply {
            good.isChecked = false
            damaged.isChecked = false
            repaired.isChecked = false
            scraped.isChecked = false
            editTextAlbumDesc.text.clear()
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
                            it.response.let {
                                Toast.makeText(
                                    requireActivity(),
                                    "Item inspected successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isInspected = true
                                this@AddInspectionFragment.dismiss()
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

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListner.invoke(isInspected)
        super.onDismiss(dialog)
    }

    private fun checkValidation(){
        binding.apply {
            if (editTextAlbumDesc.text.trim().isEmpty().not() || state == "CLEANED") {
                save.isEnabled = true
                save.isClickable = true
            } else {
                save.isEnabled = false
                save.isClickable = false
            }
        }
    }
}