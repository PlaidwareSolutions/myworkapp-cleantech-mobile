package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactX(
    @SerialName("active") var active: Boolean = false,
    @SerialName("address") var address: Address? = null,
    @SerialName("businessDetails") var businessDetails: String = "",
    @SerialName("createdAt") var createdAt: String = "",
    @SerialName("email") var email: List<String> = listOf(),
    @SerialName("_id") var id: String = "",
    @SerialName("name") var name: String = "",
    @SerialName("phone") var phone: List<String> = listOf(),
    @SerialName("systemUser") var systemUser: SystemUser = SystemUser(),
    @SerialName("type") var type: Type = Type(),
    @SerialName("updatedAt") var updatedAt: String = "",
    @SerialName("updatedBy") var updatedBy: String = "",
    @SerialName("__v") var v: Int = 0
)