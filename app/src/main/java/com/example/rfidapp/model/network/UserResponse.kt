package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("contact") var contact: ContactX = ContactX(),
    @SerialName("success") var success: Boolean = false
)