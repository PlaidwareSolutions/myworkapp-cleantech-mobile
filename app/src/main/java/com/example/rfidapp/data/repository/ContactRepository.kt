package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.ContactApi
import com.example.rfidapp.model.network.CreateContactRequest
import com.example.rfidapp.model.network.CreateContactResponse
import javax.inject.Inject

class ContactRepository @Inject constructor(private val contactApi: ContactApi) {

    suspend fun createContact(token: String, contactRequest: CreateContactRequest): CreateContactResponse {
        return contactApi.createContact(token, contactRequest)
    }
}