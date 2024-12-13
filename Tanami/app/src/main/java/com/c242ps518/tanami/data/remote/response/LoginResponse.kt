package com.c242ps518.tanami.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("data")
    val responseData: LoginData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class LoginData(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)
