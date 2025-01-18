package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateShipmentRequest
import com.example.rfidapp.model.network.CreateShipmentResponse
import com.example.rfidapp.model.network.PdfData
import com.example.rfidapp.model.network.ReceiveShipmentRequest
import com.example.rfidapp.model.network.ReceiveShipmentResponse
import com.example.rfidapp.model.network.Shipment
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShipmentApi {
    @POST("v1/shipment/create")
    suspend fun createShipment(
        @Header("authorization") token: String,
        @Body shipmentRequest: CreateShipmentRequest
    ): ApiResponse<CreateShipmentResponse>

    @PUT("v1/shipment/{shipmentId}")
    suspend fun updateShipment(
        @Header("authorization") token: String,
        @Path("shipmentId") shipmentId: String,
        @Body shipmentRequest: CreateShipmentRequest
    ): ApiResponse<CreateShipmentResponse>

    @GET("v1/shipment/")
    suspend fun getShipments(
        @Header("authorization") token: String
    ): ApiResponse<ArrayList<Shipment>>

    @GET("v1/shipment/{shipmentId}")
    suspend fun getShipmentById(
        @Header("authorization") token: String,
        @Path("shipmentId") shipmentId: String
    ): ApiResponse<Shipment>

    @GET("v1/shipment/{shipmentId}/pdf")
    suspend fun getShipmentIdPdf(
        @Path("shipmentId") shipmentId: String,
        @Header("authorization") token: String
    ): ApiResponse<PdfData>

    @POST("v1/shipment/receive/{shipmentId}")
    suspend fun receiveShipment(
        @Header("authorization") token: String,
        @Path("shipmentId") shipmentId: String,
        @Body receiveShipmentRequest: ReceiveShipmentRequest
    ): ApiResponse<ReceiveShipmentResponse>
}
