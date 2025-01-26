package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.BolRepository
import com.example.rfidapp.model.network.Bol
import com.example.rfidapp.model.network.BolX
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
class BolViewModel @Inject constructor(private val bolRepository: BolRepository) : ViewModel(){

    private val _getBol = MutableStateFlow<ScreenState<List<BolX>>>(ScreenState.Idle)
    val getBol: StateFlow<ScreenState<List<BolX>>> = _getBol.asStateFlow()

    fun fetchBolList(
        orderNumber: String? = null,
        customer: String? = null,
        carrier: String? = null,
        bolNumber: String? = null,
        shipmentNumber: String? = null,
    ) {
        viewModelScope.launch {
            _getBol.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = bolRepository.fetchBolList(
                        token = token,
                        orderNumber = orderNumber,
                        customer = customer,
                        carrier = carrier,
                        bolNumber = bolNumber,
                        shipmentNumber = shipmentNumber
                    )
                    if (response.isSuccess()) {
                        _getBol.value = ScreenState.Success(response.data?: listOf())
                    } else {
                        _getBol.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _getBol.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

}