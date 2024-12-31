package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderResponse(
    @SerializedName("data")
    var order: Order? = Order(),
    @SerializedName("success")
    var success: Boolean? = false
)