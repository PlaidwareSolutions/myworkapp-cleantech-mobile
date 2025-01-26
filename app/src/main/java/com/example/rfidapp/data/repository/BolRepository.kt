package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.BolApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.Asset
import com.example.rfidapp.model.network.AssetInspectionRequest
import com.example.rfidapp.model.network.AssetInspectionResponse
import com.example.rfidapp.model.network.Bol
import javax.inject.Inject

class BolRepository @Inject constructor(private val bolApi: BolApi) {

    suspend fun fetchBolList(
        token: String,
    ): ApiResponse<List<Bol>> {
        return bolApi.fetchBolList(token)
    }


}
