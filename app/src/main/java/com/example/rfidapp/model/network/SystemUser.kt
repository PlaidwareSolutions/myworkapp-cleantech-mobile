package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemUser(
    @SerialName("active") var active: Boolean = false,
    @SerialName("_id") var id: String = "",
    @SerialName("password") var password: Password = Password(),
    @SerialName("username") var username: String = ""
)