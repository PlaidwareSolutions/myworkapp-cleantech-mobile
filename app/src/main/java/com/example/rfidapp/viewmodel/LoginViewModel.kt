package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.UserRepository
import com.example.rfidapp.model.network.LoginResponse
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<ScreenState<LoginResponse>>(ScreenState.Idle)
    val loginState: StateFlow<ScreenState<LoginResponse>> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ScreenState.Loading
            try {
                val response = userRepository.login(username, password)
                if (response.isSuccess()){
                    response.data?.let {
                        SharedPrefs.accessToken = it.token
                        _loginState.value = ScreenState.Success(it)
                    }
                }else{

                }
            } catch (e: Exception) {
                _loginState.value = ScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }
}