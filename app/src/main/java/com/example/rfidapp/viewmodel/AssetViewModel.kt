package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.AssetRepository
import com.example.rfidapp.model.network.Asset
import com.example.rfidapp.model.network.AssetInspectionRequest
import com.example.rfidapp.model.network.AssetInspectionResponse
import com.example.rfidapp.model.network.AssetListResponse
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import com.example.rfidapp.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(private val assetRepository: AssetRepository) : ViewModel() {

    private val _assetInspection = MutableStateFlow<ScreenState<AssetInspectionResponse>>(ScreenState.Idle)
    val assetInspection: StateFlow<ScreenState<AssetInspectionResponse>> = _assetInspection.asStateFlow()


    private val _assetHistory = MutableStateFlow<ScreenState<List<Asset>>>(ScreenState.Idle)
    val assetHistory: StateFlow<ScreenState<List<Asset>>> = _assetHistory.asStateFlow()

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

    fun getAssetByTagID(tagIds: com.google.gson.JsonArray) {
        viewModelScope.launch {
            _assetHistory.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = assetRepository.getAssetByTagID(
                        token,
                        tagIds = tagIds
                    )
                    if (response.isSuccess()) {
                        _assetHistory.value =
                            ScreenState.Success(response.data ?: listOf(Asset()))
                    } else {
                        _assetHistory.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _assetHistory.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }


}