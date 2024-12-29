package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactUpdateRequest(
    @SerialName("active") var active: Boolean = false,
    @SerialName("address") var address: Address? = null,
    @SerialName("businessDetails") var businessDetails: String = "",
    @SerialName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
    @SerialName("email") var email: List<String> = listOf(),
    @SerialName("name") var name: String = "",
    @SerialName("phone") var phone: List<String> = listOf()
)