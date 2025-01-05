package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    @SerialName("dl") var dl: String? = null,
    @SerialName("name") var name: String? = null
)