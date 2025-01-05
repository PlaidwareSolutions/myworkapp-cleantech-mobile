package com.example.rfidapp.data.network

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

interface ProductApi {

    @POST("v1/pr oduct/create")
    suspend fun createProduct(
        @Header("authorization") token: String,
        @Body request: ProductRequest
    ): ProductResponse

    @PUT("v1/product/{id}")
    suspend fun updateProduct(
        @Header("authorization") token: String,
        @Path("id") productId: String,
        @Body request: UpdateProductRequest
    ): UpdateProductResponse

    @GET("v1/product")
    suspend fun getProducts(
        @Header("authorization") token: String
    ): ProductListResponse
}