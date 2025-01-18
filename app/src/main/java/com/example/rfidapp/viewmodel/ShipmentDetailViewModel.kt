package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ShipmentRepository
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.model.network.PdfData
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import com.example.rfidapp.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentDetailViewModel @Inject constructor(private val shipmentRepository: ShipmentRepository) :
    ViewModel() {

    private val _shipment: MutableStateFlow<ScreenState<Shipment?>> =
        MutableStateFlow(ScreenState.Idle)
    val shipment: StateFlow<ScreenState<Shipment?>> = _shipment.asStateFlow()

    fun fetchShipmentDetail(shipmentId: String) {
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
                    _shipment.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun fetchShipmentPdf(): Flow<ApiResponse<PdfData>> = flow {
        try {
            SharedPrefs.accessToken?.let { token ->
                if (_shipment.value is ScreenState.Success) {
                    val response = shipmentRepository.getShipmentPdf(
                        (_shipment.value as ScreenState.Success<Shipment?>).response?.id
                            ?: "", token
                    )
                    emit(response)
                }
            }
        } catch (e: Exception) {
            emit(ApiResponse(success = false, data = null)) // Handle error response appropriately
        }
    }
}