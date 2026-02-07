package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateOrderRequest
import com.example.rfidapp.model.network.CreateOrderResponse
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.model.network.PdfData
import com.example.rfidapp.model.network.UpdateOrderRequest
import com.example.rfidapp.model.network.orderdetail.OrderDetail
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
        @Header("authorization") token: String,
        @Query("carrier") carrierId: String? = null,
        @Query("referenceId") referenceId: String? = null,
        @Query("customer") customerId: String? = null,
        @Query("status") status: String? = "APPROVED,SHIPPED-PARTIAL",
        @Query("type") orderType: String? = "OUTBOUND"
    ): ApiResponse<ArrayList<Order>>

    @GET("v1/order/{orderId}")
    suspend fun getOrderDetail(
        @Path("orderId") orderId: String,
        @Header("authorization") token: String
    ): ApiResponse<OrderDetail>

    @GET("v1/order/{orderId}/pdf")
    suspend fun getOrderPdf(
        @Path("orderId") orderId: String,
        @Header("authorization") token: String
    ): ApiResponse<PdfData>
}