package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
   @SerializedName("contact") var contact: Contact = Contact(),
   @SerializedName("message") var message: String = "",
   @SerializedName("success") var success: Boolean = false,
   @SerializedName("token") var token: String = ""
) {
    data class Contact(
       @SerializedName("active") var active: Boolean = false,
       @SerializedName("address") var address: Address? = null,
       @SerializedName("createdAt") var createdAt: String = "",
       @SerializedName("email") var email: List<String> = listOf(),
       @SerializedName("_id") var id: String = "",
       @SerializedName("name") var name: String = "",
       @SerializedName("phone") var phone: List<String> = listOf(),
       @SerializedName("systemUser") var systemUser: SystemUser = SystemUser(),
       @SerializedName("type") var type: Type = Type(),
       @SerializedName("updatedAt") var updatedAt: String = "",
       @SerializedName("__v") var v: Int = 0
    ) {
        data class SystemUser(
           @SerializedName("active") var active: Boolean = false,
           @SerializedName("_id") var id: String = "",
           @SerializedName("password") var password: Password = Password(),
           @SerializedName("username") var username: String = ""
        ) {
            data class Password(
               @SerializedName("lastChanged") var lastChanged: String = ""
            )
        }
    }
}