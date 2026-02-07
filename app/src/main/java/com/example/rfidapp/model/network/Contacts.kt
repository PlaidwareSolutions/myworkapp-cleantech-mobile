package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Contacts(
   @SerializedName("data") var contactList: ArrayList<Contact> = arrayListOf(),
   @SerializedName("success") var success: Boolean = false
)