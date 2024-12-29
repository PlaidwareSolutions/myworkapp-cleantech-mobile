package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTagRequest(
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
    @SerializedName("model") var model: String? = null,
    @SerializedName("provider") var provider: String? = null,
    @SerializedName("serial") var serial: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("upc") var upc: String? = null
)