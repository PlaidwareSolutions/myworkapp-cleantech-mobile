package com.example.rfidapp.data.network

import com.example.rfidapp.model.network.CreateRoleRequest
import com.example.rfidapp.model.network.CreateRoleResponse
import com.example.rfidapp.model.network.PermissionListResponse
import com.example.rfidapp.model.network.RoleListResponse
import com.example.rfidapp.model.network.UpdateRoleRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoleApi {

    @POST("v1/user/role/create")
    suspend fun createUserRole(
        @Header("authorization") token: String,
        @Body request: CreateRoleRequest
    ): CreateRoleResponse

    @PUT("v1/user/role/{roleName}")
    suspend fun updateUserRole(
        @Header("authorization") token: String,
        @Path("roleName") roleName: String,
        @Body request: UpdateRoleRequest
    ): CreateRoleResponse

    @GET("v1/user/role")
    suspend fun getRoles(
        @Header("authorization") token: String
    ): RoleListResponse

    @GET("v1/user/role/permissions")
    suspend fun getRolePermissions(
        @Header("authorization") token: String
    ): PermissionListResponse
}