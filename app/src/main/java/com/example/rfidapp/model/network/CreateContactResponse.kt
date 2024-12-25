package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactResponse(
    @SerialName("data") var `data`: Data = Data(),
    @SerialName("success") var success: Boolean = false
){
    @Serializable
    data class Data(
        @SerialName("active") var active: Boolean = false,
        @SerialName("address") var address: Address? = null,
        @SerialName("businessDetails") var businessDetails: String = "",
        @SerialName("createdAt") var createdAt: String = "",
        @SerialName("createdBy") var createdBy: String = "",
        @SerialName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
        @SerialName("email") var email: List<String> = listOf(),
        @SerialName("_id") var id: String = "",
        @SerialName("name") var name: String = "",
        @SerialName("phone") var phone: List<String> = listOf(),
        @SerialName("type") var type: String = "",
        @SerialName("updatedAt") var updatedAt: String = "",
        @SerialName("__v") var v: Int = 0
    )
}