package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Permissions(
    @SerialName("admin") var admin: Boolean = false,
    @SerialName("Analytics") var analytics: Boolean = false,
    @SerialName("AssetCommissioning") var assetCommissioning: Boolean = false,
    @SerialName("AssetManagement") var assetManagement: Boolean = false,
    @SerialName("HierarchyManagement") var hierarchyManagement: Boolean = false,
    @SerialName("OrderCreateAll") var orderCreateAll: Boolean = false,
    @SerialName("OrderCreateSelf") var orderCreateSelf: Boolean = false,
    @SerialName("OrderManagement") var orderManagement: Boolean = false,
    @SerialName("OrderViewAll") var orderViewAll: Boolean = false,
    @SerialName("OrderViewSelf") var orderViewSelf: Boolean = false,
    @SerialName("ProductsManagement") var productsManagement: Boolean = false,
    @SerialName("Shipping") var shipping: Boolean = false,
    @SerialName("UserManagement") var userManagement: Boolean = false
)