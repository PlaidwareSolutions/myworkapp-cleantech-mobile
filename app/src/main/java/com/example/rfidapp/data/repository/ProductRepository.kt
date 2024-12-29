package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.ProductApi
import com.example.rfidapp.model.network.ProductListResponse
import com.example.rfidapp.model.network.ProductRequest
import com.example.rfidapp.model.network.ProductResponse
import com.example.rfidapp.model.network.UpdateProductRequest
import com.example.rfidapp.model.network.UpdateProductResponse
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productApi: ProductApi) {

    suspend fun createProduct(
        token: String,
        name: String,
        description: String,
        customAttributes: Map<String, String>,
        active: Boolean
    ): ProductResponse {
        return productApi.createProduct(
            token = token,
            request = ProductRequest(name, description, customAttributes, active)
        )
    }

    suspend fun updateProduct(
        token: String,
        productId: String,
        name: String,
        description: String,
        customAttributes: Map<String, String>
    ): UpdateProductResponse {
        val request = UpdateProductRequest(name, description, customAttributes)
        return productApi.updateProduct(token, productId, request)
    }

    suspend fun fetchProducts(token: String): ProductListResponse {
        return productApi.getProducts(token)
    }
}