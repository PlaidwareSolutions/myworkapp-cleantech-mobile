package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Contact(
   @SerializedName("active") var active: Boolean = false,
   @SerializedName("address") var address: Address? = null,
   @SerializedName("businessDetails") var businessDetails: String = "",
   @SerializedName("createdAt") var createdAt: String = "",
   @SerializedName("createdBy") var createdBy: String = "",
   @SerializedName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
   @SerializedName("email") var email: List<String> = listOf(),
   @SerializedName("_id") var id: String = "",
   @SerializedName("name") var name: String = "",
   @SerializedName("phone") var phone: List<String> = listOf(),
   @SerializedName("type") var type: String = "",
   @SerializedName("updatedAt") var updatedAt: String = "",
   @SerializedName("updatedBy") var updatedBy: String = "",
   @SerializedName("__v") var v: Int = 0
)