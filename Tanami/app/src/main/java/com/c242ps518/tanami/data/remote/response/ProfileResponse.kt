package com.c242ps518.tanami.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

    @field:SerializedName("data")
    val dataProfile: DataProfile? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataProfile(

    @field:SerializedName("profilePicture")
    val profilePicture: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null
)
