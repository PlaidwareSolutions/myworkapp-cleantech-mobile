package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.AssetInspectionRequest
import com.example.rfidapp.model.network.AssetInspectionResponse
import com.example.rfidapp.model.network.AssetListResponse
import com.example.rfidapp.model.network.CreateAssetRequest
import com.example.rfidapp.model.network.CreateAssetResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AssetApi {
    @POST("v1/asset/create")
    suspend fun createAsset(
        @Header("authorization") token: String,
        @Body assetRequest: CreateAssetRequest
    ): ApiResponse<CreateAssetResponse>

    @PUT("v1/asset/{id}")
    suspend fun updateAsset(
        @Header("authorization") token: String,
        @Path("id") assetId: String,
        @Body assetUpdateRequest: CreateAssetRequest
    ): ApiResponse<CreateAssetResponse>

    @GET("v1/asset")
    suspend fun getAssets(
        @Header("authorization") token: String
    ): ApiResponse<AssetListResponse>

    @POST("v1/asset/inspect")
    suspend fun assetInspection(
        @Header("authorization") token: String,
        @Body assetInspectionRequest: AssetInspectionRequest
    ): ApiResponse<AssetInspectionResponse>
}