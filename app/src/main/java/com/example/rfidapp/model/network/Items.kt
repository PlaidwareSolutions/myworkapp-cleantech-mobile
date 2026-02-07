package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Items(
    @SerializedName("product") var product: String? = null,
    @SerializedName("quantity") var quantity: String? = null
)