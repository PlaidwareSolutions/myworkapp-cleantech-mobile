package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PermissionListResponse(
    @SerialName("data") var permissionList: ArrayList<String> = arrayListOf(),
    @SerialName("success") var success: Boolean = false
)