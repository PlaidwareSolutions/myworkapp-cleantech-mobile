package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.BolApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.BolX
import javax.inject.Inject

class BolRepository @Inject constructor(private val bolApi: BolApi) {

    suspend fun fetchBolList(
        token: String,
        orderNumber: String? = null,
        customer: String? = null,
        carrier: String? = null,
        bolNumber: String? = null,
        shipmentNumber: String? = null,
    ): ApiResponse<List<BolX>> {
        return bolApi.fetchBolList(
            authorization = token,
            orderNumber = orderNumber,
            customer = customer,
            carrier = carrier,
            bolNumber = bolNumber,
            shipmentNumber = shipmentNumber
        )
    }


}
