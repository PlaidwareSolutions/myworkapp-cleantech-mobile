package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.AssetApi
import com.example.rfidapp.data.network.TagApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.Asset
import com.example.rfidapp.model.network.AssetInspectionRequest
import com.example.rfidapp.model.network.AssetInspectionResponse
import com.example.rfidapp.model.network.AssetListResponse
import com.example.rfidapp.model.network.CreateTagRequest
import com.example.rfidapp.model.network.CreateTagResponse
import com.example.rfidapp.model.network.FetchTagsResponse
import com.example.rfidapp.model.network.UpdateTagRequest
import javax.inject.Inject

class AssetRepository @Inject constructor(private val assetApi: AssetApi) {

    suspend fun assetInspection(
        token: String,
        request: AssetInspectionRequest
    ): ApiResponse<AssetInspectionResponse> {
        return assetApi.assetInspection(token, request)
    }

    suspend fun getAssetByTagID(
        token: String,
        tagId: String
    ): ApiResponse<List<Asset>> {
        return assetApi.getAssetsByTagID(token, tagId)
    }

}
