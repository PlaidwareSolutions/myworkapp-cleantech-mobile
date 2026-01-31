package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.BolX
import com.example.rfidapp.model.network.PdfData
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface BolApi {
    @GET("v1/bol/")
    suspend fun fetchBolList(
        @Header("authorization") authorization: String,
        @Query("orderNumber") orderNumber: String? = null,
        @Query("customer") customer: String? = null,
        @Query("carrier") carrier: String? = null,
        @Query("bolNumber") bolNumber: String? = null,
        @Query("shipmentNumber") shipmentNumber: String? = null,
    ): ApiResponse<List<BolX>>

    @GET("v1/bol/{bolId}/pdf")
    suspend fun getBolPdf(
        @Path("bolId") bolId: String,
        @Header("authorization") token: String
    ): ApiResponse<PdfData>
}