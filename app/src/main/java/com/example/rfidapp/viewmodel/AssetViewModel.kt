package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.AssetRepository
import com.example.rfidapp.model.network.AssetInspectionRequest
import com.example.rfidapp.model.network.AssetInspectionResponse
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
class AssetViewModel @Inject constructor(private val assetRepository: AssetRepository) : ViewModel() {

    private val _assetInspection = MutableStateFlow<ScreenState<AssetInspectionResponse>>(ScreenState.Idle)
    val assetInspection: StateFlow<ScreenState<AssetInspectionResponse>> = _assetInspection.asStateFlow()

    fun assetInspection(request: AssetInspectionRequest){
        viewModelScope.launch {
            _assetInspection.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = assetRepository.assetInspection(token,
                        request = request
                    )
                    if (response.isSuccess()) {
                        _assetInspection.value = ScreenState.Success(response.data?: AssetInspectionResponse())
                    } else {
                        _assetInspection.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _assetInspection.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

}