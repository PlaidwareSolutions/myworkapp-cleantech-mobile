package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("customAttributes") val customAttributes: Map<String, String>,
    @SerializedName("active") val active: Boolean,
    @SerializedName("createdAt") val createdAt: String
)