package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailResponse(
    @SerialName("success") var success: Boolean = false,
    @SerialName("data") var orderDetail: OrderDetail? = null
)