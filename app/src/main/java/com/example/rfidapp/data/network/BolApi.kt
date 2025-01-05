package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.Bol
import retrofit2.http.GET
import retrofit2.http.Header

interface BolApi {
    @GET("bol/")
    suspend fun fetchBolList(
        @Header("authorization") authorization: String
    ): ApiResponse<List<Bol>>
}