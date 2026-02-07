package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.RoleApi
import com.example.rfidapp.model.network.CreateRoleRequest
import com.example.rfidapp.model.network.CreateRoleResponse
import com.example.rfidapp.model.network.PermissionListResponse
import com.example.rfidapp.model.network.RoleListResponse
import com.example.rfidapp.model.network.UpdateRoleRequest
import javax.inject.Inject

class RoleRepository @Inject constructor(private val roleApi: RoleApi) {

    suspend fun createRole(
        token: String,
        roleName: String,
        map: Map<String, Boolean>
    ): CreateRoleResponse {
        val request = CreateRoleRequest(
            name = roleName,
            permissions = map
        )
        return roleApi.createUserRole(token, request)
    }

    suspend fun updateRole(
        token: String,
        roleName: String,
        map: Map<String, Boolean>
    ): CreateRoleResponse {
        val request = UpdateRoleRequest(permissions = map)
        return roleApi.updateUserRole(token, roleName, request)
    }

    suspend fun fetchRoles(token: String): RoleListResponse {
        return roleApi.getRoles(token)
    }

    suspend fun fetchRolePermissions(token: String): PermissionListResponse {
        return roleApi.getRolePermissions(token)
    }
}
