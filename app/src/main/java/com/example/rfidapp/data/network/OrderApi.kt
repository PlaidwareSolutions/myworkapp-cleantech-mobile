package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateOrderRequest
import com.example.rfidapp.model.network.CreateOrderResponse
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.model.network.OrderDetailResponse
import com.example.rfidapp.model.network.OrderListResponse
import com.example.rfidapp.model.network.UpdateOrderRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderApi {
    @POST("v1/order/create")
    suspend fun createOrder(
        @Header("authorization") token: String,
        @Body orderRequest: CreateOrderRequest
    ): CreateOrderResponse

    @PUT("v1/order/{id}")
    suspend fun updateOrder(
        @Header("authorization") token: String,
        @Path("id") orderId: String,
        @Body orderUpdateRequest: UpdateOrderRequest
    ): CreateOrderResponse

    @GET("v1/order/")
    suspend fun getOrders(
        @Header("authorization") token: String
    ): ApiResponse<ArrayList<Order>>

    @GET("v1/order/{orderId}")
    suspend fun getOrderDetail(
        @Path("orderId") orderId: String,
        @Header("authorization") token: String
    ): OrderDetailResponse

    @GET("v1/order/{orderId}/pdf")
    suspend fun downloadOrderPdf(
        @Path("orderId") orderId: String,
        @Header("authorization") token: String
    ): ResponseBody
}