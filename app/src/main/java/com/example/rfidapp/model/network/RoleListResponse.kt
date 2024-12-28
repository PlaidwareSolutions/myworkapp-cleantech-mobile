package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleListResponse(
    @SerialName("data") var roleList: ArrayList<Role> = arrayListOf(),
    @SerialName("success") var success: Boolean = false
)