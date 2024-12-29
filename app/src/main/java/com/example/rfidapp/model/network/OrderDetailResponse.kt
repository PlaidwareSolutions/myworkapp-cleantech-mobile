package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailResponse(
    @SerializedName("success") var success: Boolean = false,
    @SerializedName("data") var orderDetail: OrderDetail? = null
)