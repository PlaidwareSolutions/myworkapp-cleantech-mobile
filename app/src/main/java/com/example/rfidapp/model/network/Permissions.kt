package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Permissions(
    @SerializedName("admin") var admin: Boolean = false,
    @SerializedName("Analytics") var analytics: Boolean = false,
    @SerializedName("AssetCommissioning") var assetCommissioning: Boolean = false,
    @SerializedName("AssetManagement") var assetManagement: Boolean = false,
    @SerializedName("HierarchyManagement") var hierarchyManagement: Boolean = false,
    @SerializedName("OrderCreateAll") var orderCreateAll: Boolean = false,
    @SerializedName("OrderCreateSelf") var orderCreateSelf: Boolean = false,
    @SerializedName("OrderManagement") var orderManagement: Boolean = false,
    @SerializedName("OrderViewAll") var orderViewAll: Boolean = false,
    @SerializedName("OrderViewSelf") var orderViewSelf: Boolean = false,
    @SerializedName("ProductsManagement") var productsManagement: Boolean = false,
    @SerializedName("Shipping") var shipping: Boolean = false,
    @SerializedName("UserManagement") var userManagement: Boolean = false
)