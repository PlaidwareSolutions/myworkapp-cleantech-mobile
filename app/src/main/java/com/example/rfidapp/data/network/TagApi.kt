package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateTagRequest
import com.example.rfidapp.model.network.CreateTagResponse
import com.example.rfidapp.model.network.FetchTagsResponse
import com.example.rfidapp.model.network.UpdateTagRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TagApi {
    @POST("v1/tag/create")
    suspend fun createTag(
        @Header("authorization") token: String,
        @Body tagRequest: CreateTagRequest
    ): ApiResponse<CreateTagResponse>

    @PUT("v1/tag/{id}")
    suspend fun updateTag(
        @Header("authorization") token: String,
        @Path("id") tagId: String,
        @Body tagUpdateRequest: UpdateTagRequest
    ):  ApiResponse<CreateTagResponse>


    @GET("v1/tag")
    suspend fun getTags(
        @Header("authorization") token: String
    ):  ApiResponse<FetchTagsResponse>
}