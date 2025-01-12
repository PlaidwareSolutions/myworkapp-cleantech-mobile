package com.example.rfidapp.util.core

import androidx.lifecycle.asLiveData
import com.example.rfidapp.model.network.CreateShipmentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ShipmentUtil {

    private val _createShipment= MutableStateFlow<CreateShipmentRequest?>(null)
    val createShipment: StateFlow<CreateShipmentRequest?> = _createShipment.asStateFlow()
    val createShipmentListLiveData = _createShipment.asLiveData()


    fun setCreateShipment(createShipmentRequest: CreateShipmentRequest){
        _createShipment.value = createShipmentRequest
    }

}