package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("location") val location: Location
)