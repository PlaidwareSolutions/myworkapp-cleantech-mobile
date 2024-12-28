package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoleRequest(
    @SerialName("name") var name: String = "",
    @SerialName("permissions") var permissions: Map<String, Boolean> = mapOf()
)