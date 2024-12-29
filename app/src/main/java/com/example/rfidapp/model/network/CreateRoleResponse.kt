package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoleResponse(
    @SerializedName("data") var role: Role = Role(),
    @SerializedName("success") var success: Boolean = false
)