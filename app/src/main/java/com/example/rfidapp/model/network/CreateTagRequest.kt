package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTagRequest(
   @SerializedName("brand") var brand: String? = "",
   @SerializedName("customAttributes") var customAttributes: Map<String, String> = mapOf(),
   @SerializedName("epc") var epc: String? = "",
   @SerializedName("model") var model: String? = "",
   @SerializedName("provider") var provider: String? = "",
   @SerializedName("serial") var serial: String? = "",
   @SerializedName("type") var type: String? = "",
   @SerializedName("upc") var upc: String? = ""
)