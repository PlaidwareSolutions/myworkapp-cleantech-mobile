package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTagResponse(
   @SerializedName("data") var tag: Tag? = null,
   @SerializedName("success") var success: Boolean? = null
)