package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PrepareShipmentViewModel @Inject constructor() : ViewModel() {

    val selectedOrder: MutableStateFlow<Int?> = MutableStateFlow(null)

}