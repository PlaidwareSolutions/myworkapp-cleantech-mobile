package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Password(
    @SerialName("lastChanged") var lastChanged: String = ""
)