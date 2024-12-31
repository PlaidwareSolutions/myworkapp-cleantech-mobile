package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactResponse(
   @SerializedName("data") var contact: Contact = Contact(),
   @SerializedName("success") var success: Boolean = false
)