package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTagRequest(
    @SerialName("brand") var brand: String? = null,
    @SerialName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
    @SerialName("model") var model: String? = null,
    @SerialName("provider") var provider: String? = null,
    @SerialName("serial") var serial: String? = null,
    @SerialName("type") var type: String? = null,
    @SerialName("upc") var upc: String? = null
)