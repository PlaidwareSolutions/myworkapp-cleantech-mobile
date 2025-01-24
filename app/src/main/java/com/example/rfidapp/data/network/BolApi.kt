package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.Bol
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BolApi {
    @GET("bol/")
    suspend fun fetchBolList(
        @Header("authorization") authorization: String,
        @Query("orderNumber") orderNumber: String? = "",
        @Query("customer") customer: String? = "",
        @Query("carrier") carrier: String? = "",
        @Query("bolNumber") bolNumber: String? = "",
        @Query("shipmentNumber") shipmentNumber: String? = "",
    ): ApiResponse<List<Bol>>
}