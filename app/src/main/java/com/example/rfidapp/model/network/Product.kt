package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("_id") var id: String? = null,
    @SerialName("name") var name: String? = null
)