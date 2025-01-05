package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateShipmentResponse(
    @SerialName("bols") var bols: List<String>? = listOf(),
    @SerialName("carrier") var carrier: String? = "",
    @SerialName("createdAt") var createdAt: String? = "",
    @SerialName("createdBy") var createdBy: String? = "",
    @SerialName("driver") var driver: Driver? = null,
    @SerialName("_id") var id: String? = "",
    @SerialName("referenceId") var referenceId: String? = "",
    @SerialName("shipmentDate") var shipmentDate: String? = "",
    @SerialName("updatedAt") var updatedAt: String? = "",
    @SerialName("__v") var v: Int? = 0
)