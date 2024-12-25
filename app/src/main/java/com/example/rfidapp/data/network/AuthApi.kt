package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.LoginRequest
import com.example.rfidapp.model.network.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("v1/user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
