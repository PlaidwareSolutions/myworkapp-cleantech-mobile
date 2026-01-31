package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactRequest(
   @SerializedName("active") val active: Boolean,
   @SerializedName("type") val type: String,
   @SerializedName("name") val name: String,
   @SerializedName("businessDetails") val businessDetails: String,
   @SerializedName("address") val address: Address,
   @SerializedName("phone") val phone: List<String>,
   @SerializedName("email") val email: List<String>,
   @SerializedName("customAttributes") val customAttributes: Map<String, String>
)