package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contacts(
    @SerialName("data") var contactList: ArrayList<Contact> = arrayListOf(),
    @SerialName("success") var success: Boolean = false
)