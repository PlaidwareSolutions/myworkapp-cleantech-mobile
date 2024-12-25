package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.CreateOrderRequest
import com.example.rfidapp.model.network.CreateOrderResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderApi {
    @POST("v1/order/create")
    suspend fun createOrder(
        @Header("authorization") token: String,
        @Body orderRequest: CreateOrderRequest
    ): CreateOrderResponse
}