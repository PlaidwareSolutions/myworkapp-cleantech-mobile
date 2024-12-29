package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.OrderApi
import com.example.rfidapp.model.network.CreateOrderRequest
import com.example.rfidapp.model.network.CreateOrderResponse
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.model.network.OrderDetailResponse
import com.example.rfidapp.model.network.UpdateOrderRequest
import java.io.File
import java.io.FileOutputStream
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

    suspend fun getOrders(token: String): List<Order> {
        return orderApi.getOrders(token)
    }

    suspend fun getOrderDetail(orderId: String, token: String): OrderDetailResponse {
        return orderApi.getOrderDetail(orderId, token)
    }

    suspend fun downloadOrderPdf(orderId: String, token: String, file: File): Boolean {
        return try {
            val response = orderApi.downloadOrderPdf(orderId, token)
            val inputStream = response.byteStream()
            val outputStream = FileOutputStream(file)

            outputStream.use { out ->
                inputStream.copyTo(out)
            }

            true // Download successful
        } catch (e: Exception) {
            e.printStackTrace()
            false // Download failed
        }
    }
}