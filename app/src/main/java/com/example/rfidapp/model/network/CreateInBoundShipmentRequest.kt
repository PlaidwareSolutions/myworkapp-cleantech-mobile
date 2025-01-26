package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInBoundShipmentRequest(
    @SerializedName("bols") var bols: List<Bol>? = listOf(),
    @SerializedName("carrier") var carrier: String? = "",
    @SerializedName("driver") var driver: Driver? = Driver(),
    @SerializedName("shipmentDate") var shipmentDate: String? = ""
)