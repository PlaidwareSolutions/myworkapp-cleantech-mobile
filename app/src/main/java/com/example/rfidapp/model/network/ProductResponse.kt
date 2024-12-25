package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("customAttributes") val customAttributes: Map<String, String>,
    @SerialName("active") val active: Boolean,
    @SerialName("createdAt") val createdAt: String
)