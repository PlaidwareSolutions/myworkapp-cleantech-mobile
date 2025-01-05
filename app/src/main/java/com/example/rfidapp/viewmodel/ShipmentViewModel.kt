package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ShipmentRepository
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import com.example.rfidapp.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentViewModel @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) : ViewModel() {

    private val _shipmentList = MutableStateFlow<ScreenState<List<Shipment>>>(ScreenState.Idle)
    val shipmentList: StateFlow<ScreenState<List<Shipment>>> = _shipmentList.asStateFlow()

    init {
        getShipments()
    }

    private fun getShipments() {
        viewModelScope.launch {
            _shipmentList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = shipmentRepository.getShipments(token)
                    if (response.isSuccess()) {
                        _shipmentList.value = ScreenState.Success(response.data ?: emptyList())
                    } else {
                        _shipmentList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _shipmentList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }
}
