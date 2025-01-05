package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ShipmentRepository
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentDetailViewModel @Inject constructor(private val shipmentRepository: ShipmentRepository) :
    ViewModel() {

    private val _shipment: MutableStateFlow<ScreenState<Shipment?>> =
        MutableStateFlow(ScreenState.Idle)
    val shipment: StateFlow<ScreenState<Shipment?>> = _shipment.asStateFlow()

    fun fetchOrderDetail(shipmentId: String) {
        viewModelScope.launch {
            _shipment.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    _shipment.value =
                        ScreenState.Success(
                            shipmentRepository.getShipmentById(
                                token = token,
                                shipmentId = shipmentId
                            ).data
                        )
                } catch (e: Exception) {
                    _shipment.value = ScreenState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }
}