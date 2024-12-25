package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomAttributes(
    @SerialName("randomnumber")
    var randomnumber: Int = 0,
    @SerialName("somekey")
    var somekey: String = ""
)