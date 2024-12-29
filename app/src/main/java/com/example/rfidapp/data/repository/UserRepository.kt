package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.AuthApi
import com.example.rfidapp.model.network.LoginRequest
import com.example.rfidapp.model.network.LoginResponse
import javax.inject.Inject

class UserRepository @Inject constructor(private val authApi: AuthApi) {

    suspend fun login(username: String, password: String): LoginResponse {
        return authApi.login(LoginRequest(username = username, password =  password))
    }
}