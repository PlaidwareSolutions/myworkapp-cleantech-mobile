package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Password(
    @SerializedName("lastChanged") var lastChanged: String = ""
)