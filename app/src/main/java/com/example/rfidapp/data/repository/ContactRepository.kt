package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.ContactApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.Contact
import com.example.rfidapp.model.network.ContactUpdateRequest
import com.example.rfidapp.model.network.CreateContactRequest
import com.example.rfidapp.model.network.CreateContactResponse
import javax.inject.Inject

class ContactRepository @Inject constructor(private val contactApi: ContactApi) {

    suspend fun createContact(token: String, contactRequest: CreateContactRequest): CreateContactResponse {
        return contactApi.createContact(token, contactRequest)
    }

    suspend fun updateContact(token: String, contactId: String, contactUpdateRequest: ContactUpdateRequest): CreateContactResponse {
        return contactApi.updateContact(token, contactId, contactUpdateRequest)
    }

    suspend fun fetchContacts(token: String): ApiResponse<ArrayList<Contact>> {
        return contactApi.fetchContacts(token)
    }
}