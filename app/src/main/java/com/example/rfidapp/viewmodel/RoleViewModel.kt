package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.RoleRepository
import com.example.rfidapp.model.network.CreateRoleResponse
import com.example.rfidapp.util.KeyConstants.IS_ADMIN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(private val roleRepository: RoleRepository) : ViewModel() {

    private val _roleState = MutableStateFlow<CreateRoleResponse?>(null)
    val roleState: StateFlow<CreateRoleResponse?> = _roleState

    fun createRole(token: String, roleName: String) {
        viewModelScope.launch {
            try {
                val permissionMap: HashMap<String, Boolean> = hashMapOf()
                permissionMap[IS_ADMIN] = true
                val response = roleRepository.createRole(token, roleName, permissionMap)
                _roleState.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _roleState.value = null
            }
        }
    }
}
