package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputBol(
    @SerialName("order") var order: String? = null,
    @SerialName("tags") var tags: List<String?>? = null
)