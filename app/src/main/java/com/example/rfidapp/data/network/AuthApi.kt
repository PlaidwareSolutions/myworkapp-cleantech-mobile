package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.LoginRequest
import com.example.rfidapp.model.network.LoginResponse
import com.example.rfidapp.model.network.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("v1/user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("v1/user/me")
    suspend fun getCurrentUser(
        @Header("authorization") token: String
    ): UserResponse
}
