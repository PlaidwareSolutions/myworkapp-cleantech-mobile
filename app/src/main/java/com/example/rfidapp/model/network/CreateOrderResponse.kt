package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderResponse(
    @SerialName("data")
    var order: Order? = Order(),
    @SerialName("success")
    var success: Boolean? = false
)