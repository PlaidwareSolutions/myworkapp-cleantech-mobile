package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoleResponse(
    @SerialName("data") var role: Role = Role(),
    @SerialName("success") var success: Boolean = false
)