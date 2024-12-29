package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTagRequest(
    @SerialName("brand") var brand: String? = "",
    @SerialName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
    @SerialName("epc") var epc: String? = "",
    @SerialName("model") var model: String? = "",
    @SerialName("provider") var provider: String? = "",
    @SerialName("serial") var serial: String? = "",
    @SerialName("type") var type: String? = "",
    @SerialName("upc") var upc: String? = ""
)