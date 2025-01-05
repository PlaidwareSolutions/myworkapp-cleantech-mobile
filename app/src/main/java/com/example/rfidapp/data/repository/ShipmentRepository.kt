package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.ShipmentApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateShipmentRequest
import com.example.rfidapp.model.network.CreateShipmentResponse
import com.example.rfidapp.model.network.Shipment
import javax.inject.Inject

class ShipmentRepository @Inject constructor(private val shipmentApi: ShipmentApi) {

    suspend fun createShipment(
        token: String,
        shipmentRequest: CreateShipmentRequest
    ): ApiResponse<CreateShipmentResponse> {
        return shipmentApi.createShipment(token = token, shipmentRequest = shipmentRequest)
    }

    suspend fun updateShipment(
        token: String,
        shipmentId: String,
        shipmentRequest: CreateShipmentRequest
    ): ApiResponse<CreateShipmentResponse> {
        return shipmentApi.updateShipment(
            token = token,
            shipmentId = shipmentId,
            shipmentRequest = shipmentRequest
        )
    }

    suspend fun getShipments(token: String): ApiResponse<ArrayList<Shipment>> {
        return shipmentApi.getShipments(token = token)
    }

    suspend fun getShipmentById(token: String, shipmentId: String): ApiResponse<Shipment> {
        return shipmentApi.getShipmentById(token = token, shipmentId = shipmentId)
    }
}