package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("contact") var contact: Contact = Contact(),
    @SerialName("message") var message: String = "",
    @SerialName("success") var success: Boolean = false,
    @SerialName("token") var token: String = ""
) {
    data class Contact(
        @SerialName("active") var active: Boolean = false,
        @SerialName("address") var address: Address? = null,
        @SerialName("createdAt") var createdAt: String = "",
        @SerialName("email") var email: List<String> = listOf(),
        @SerialName("_id") var id: String = "",
        @SerialName("name") var name: String = "",
        @SerialName("phone") var phone: List<String> = listOf(),
        @SerialName("systemUser") var systemUser: SystemUser = SystemUser(),
        @SerialName("type") var type: Type = Type(),
        @SerialName("updatedAt") var updatedAt: String = "",
        @SerialName("__v") var v: Int = 0
    ) {
        data class SystemUser(
            @SerialName("active") var active: Boolean = false,
            @SerialName("_id") var id: String = "",
            @SerialName("password") var password: Password = Password(),
            @SerialName("username") var username: String = ""
        ) {
            data class Password(
                @SerialName("lastChanged") var lastChanged: String = ""
            )
        }
    }
}