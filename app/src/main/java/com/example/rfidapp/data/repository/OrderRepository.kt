package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.OrderApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateOrderRequest
import com.example.rfidapp.model.network.CreateOrderResponse
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.model.network.PdfData
import com.example.rfidapp.model.network.UpdateOrderRequest
import com.example.rfidapp.model.network.orderdetail.OrderDetail
import javax.inject.Inject

class OrderRepository @Inject constructor(private val orderApi: OrderApi) {

    suspend fun createOrder(
        token: String,
        createOrderRequest: CreateOrderRequest
    ): CreateOrderResponse {
        return orderApi.createOrder(token, createOrderRequest)
    }

    suspend fun updateOrder(
        token: String,
        orderId: String,
        orderUpdateRequest: UpdateOrderRequest
    ): CreateOrderResponse {
        return orderApi.updateOrder(token, orderId, orderUpdateRequest)
    }

    suspend fun getOrders(
        token: String,
        carrierId: String? = null,
        referenceId: String? = null,
        customerId: String? = null
    ): ApiResponse<ArrayList<Order>> {
        return orderApi.getOrders(
            token = token,
            carrierId = carrierId,
            referenceId = referenceId,
            customerId = customerId
        )
    }

    suspend fun getOrderDetail(orderId: String, token: String): ApiResponse<OrderDetail> {
        return orderApi.getOrderDetail(orderId, token)
    }

    suspend fun getOrderPdf(orderId: String, token: String): ApiResponse<PdfData> {
        return orderApi.getOrderPdf(orderId, token)
    }
}