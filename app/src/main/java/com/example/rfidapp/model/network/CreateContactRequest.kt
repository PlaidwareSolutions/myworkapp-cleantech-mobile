package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactRequest(
    @SerialName("active") val active: Boolean,
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("businessDetails") val businessDetails: String,
    @SerialName("address") val address: Address,
    @SerialName("phone") val phone: List<String>,
    @SerialName("email") val email: List<String>,
    @SerialName("customAttributes") val customAttributes: Map<String, String>
)