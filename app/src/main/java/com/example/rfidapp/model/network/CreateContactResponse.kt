package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactResponse(
    @SerialName("data") var contact: Contact = Contact(),
    @SerialName("success") var success: Boolean = false
)