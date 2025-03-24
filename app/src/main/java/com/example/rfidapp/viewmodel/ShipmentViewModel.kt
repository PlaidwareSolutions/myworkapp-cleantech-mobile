package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ShipmentRepository
import com.example.rfidapp.model.network.CreateShipmentRequest
import com.example.rfidapp.model.network.CreateShipmentResponse
import com.example.rfidapp.model.network.ReceiveShipmentRequest
import com.example.rfidapp.model.network.ReceiveShipmentResponse
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

    private val _createShipmentList = MutableStateFlow<ScreenState<CreateShipmentResponse>>(ScreenState.Idle)
    val createShipmentList: StateFlow<ScreenState<CreateShipmentResponse>> = _createShipmentList.asStateFlow()
    val createShipmentListLiveData = _createShipmentList.asLiveData()

    private val _receiveShipmentList = MutableStateFlow<ScreenState<ReceiveShipmentResponse>>(ScreenState.Idle)
    val receiveShipmentList: StateFlow<ScreenState<ReceiveShipmentResponse>> = _receiveShipmentList.asStateFlow()
    val receiveShipmentListLiveData = _receiveShipmentList.asLiveData()

    val selectedShipment: MutableStateFlow<Shipment?> = MutableStateFlow(null)


    fun getShipments() {
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

    fun createShipments(shipmentRequest: CreateShipmentRequest) {
        viewModelScope.launch {
            _createShipmentList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = shipmentRepository.createShipment(token,
                        shipmentRequest = shipmentRequest
                    )
                    if (response.isSuccess()) {
                        _createShipmentList.value = ScreenState.Success(response.data?: CreateShipmentResponse())
                    } else {
                        _createShipmentList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _createShipmentList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun updateShipments(shipmentId:String,shipmentRequest: CreateShipmentRequest) {
        viewModelScope.launch {
            _createShipmentList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = shipmentRepository.updateShipment(token, shipmentId = shipmentId ,
                        shipmentRequest = shipmentRequest
                    )
                    if (response.isSuccess()) {
                        _createShipmentList.value = ScreenState.Success(response.data?:CreateShipmentResponse())
                    } else {
                        _createShipmentList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _createShipmentList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun receiveShipments(shipmentId:String,shipmentRequest: ReceiveShipmentRequest){
        viewModelScope.launch {
            _receiveShipmentList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = shipmentRepository.receiveShipmentById(token, shipmentId = shipmentId ,
                        shipmentRequest = shipmentRequest
                    )
                    if (response.isSuccess()) {
                        _receiveShipmentList.value = ScreenState.Success(response.data ?: ReceiveShipmentResponse())
                    } else {
                        _receiveShipmentList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                }   catch (e: Exception) {
                    _receiveShipmentList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }
}
