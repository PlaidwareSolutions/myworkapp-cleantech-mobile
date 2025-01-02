package com.example.rfidapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.OrderRepository
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import com.example.rfidapp.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orderList = MutableStateFlow<ScreenState<List<Order>>>(ScreenState.Idle)
    val orderList: StateFlow<ScreenState<List<Order>>> = _orderList.asStateFlow()


    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            _orderList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token->
                try {
                    val response =  orderRepository.getOrders(token)
                    if(response.isSuccess()){
                        _orderList.value =  ScreenState.Success(response.data?: emptyList())
                    }else{
                        _orderList.value =  ScreenState.Error(message = response.message ?: "Unknown error")
                    }

                } catch (e: Exception) {
                    _orderList.value =  ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }
}