package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ContactUpdateRequest(
   @SerializedName("active") var active: Boolean = false,
   @SerializedName("address") var address: Address? = null,
   @SerializedName("businessDetails") var businessDetails: String = "",
   @SerializedName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
   @SerializedName("email") var email: List<String> = listOf(),
   @SerializedName("name") var name: String = "",
   @SerializedName("phone") var phone: List<String> = listOf()
)