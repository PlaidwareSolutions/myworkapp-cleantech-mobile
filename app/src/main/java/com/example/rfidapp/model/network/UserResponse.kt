package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerializedName("contact") var contact: ContactX = ContactX(),
    @SerializedName("success") var success: Boolean = false
)