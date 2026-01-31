package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SystemUser(
    @SerializedName("active") var active: Boolean = false,
    @SerializedName("_id") var id: String = "",
    @SerializedName("password") var password: Password = Password(),
    @SerializedName("username") var username: String = ""
)