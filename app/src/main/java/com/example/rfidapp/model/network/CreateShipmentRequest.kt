package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateShipmentRequest(
    @SerialName("bols") var bols: List<InputBol>? = listOf(),
    @SerialName("carrier") var carrier: String? = "",
    @SerialName("driver") var driver: Driver? = Driver(),
    @SerialName("shipmentDate") var shipmentDate: String? = ""
)