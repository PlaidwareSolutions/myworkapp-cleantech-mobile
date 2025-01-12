package com.example.rfidapp.util.core

import androidx.lifecycle.asLiveData
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.CreateShipmentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ShipmentUtil {

    private val _createShipment= MutableStateFlow<CreateShipmentRequest?>(null)
    val createShipment: StateFlow<CreateShipmentRequest?> = _createShipment.asStateFlow()
    val createShipmentListLiveData = _createShipment.asLiveData()

    private val _orderShipments = MutableStateFlow<ArrayList<OrderShipmentData>>(arrayListOf())
    val orderShipments: StateFlow<ArrayList<OrderShipmentData>> = _orderShipments.asStateFlow()
    val orderShipmentsListLiveData = _orderShipments.asLiveData()


    fun setCreateShipment(createShipmentRequest: CreateShipmentRequest){
        _createShipment.value = createShipmentRequest
    }

    fun addOrUpdateOrderToShipment(orderShipmentData: OrderShipmentData) {
        _orderShipments.value.firstOrNull { it.orderRefId == orderShipmentData.orderRefId }?.let {
            _orderShipments.value.indexOf(it).let { index ->
                _orderShipments.value[index] = orderShipmentData
            }
        }?:run {
            _orderShipments.value.add(orderShipmentData)
        }
    }

    fun getOrderToShipmentById(orderId: String): OrderShipmentData? {
        return _orderShipments.value.firstOrNull { it.orderId == orderId }
    }

    fun clearAll() {
        _createShipment.value = null
        _orderShipments.value.clear()
    }
}