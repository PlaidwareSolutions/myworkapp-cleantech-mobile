package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ContactUpdateRequest
import com.example.rfidapp.model.network.Contacts
import com.example.rfidapp.model.network.CreateContactRequest
import com.example.rfidapp.model.network.CreateContactResponse
import com.example.rfidapp.model.network.ProductListResponse
import com.example.rfidapp.model.network.ProductRequest
import com.example.rfidapp.model.network.ProductResponse
import com.example.rfidapp.model.network.UpdateProductRequest
import com.example.rfidapp.model.network.UpdateProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactApi {

    @POST("v1/contact/create")
    suspend fun createContact(
        @Header("authorization") token: String,
        @Body contactRequest: CreateContactRequest
    ): CreateContactResponse

    @PUT("v1/contact/{id}")
    suspend fun updateContact(
        @Header("authorization") token: String,
        @Path("id") contactId: String,
        @Body contactUpdateRequest: ContactUpdateRequest
    ): CreateContactResponse

    @GET("v1/contact")
    suspend fun getContact(
        @Header("authorization") token: String
    ): Contacts
}