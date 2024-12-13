package com.c242ps518.tanami.utils

import com.c242ps518.tanami.data.remote.response.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException


object ErrorUtil {
    fun getErrorMessage(e: Exception): String {
        return when (e) {
            is HttpException -> {
                try {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    errorBody?.error ?: "An unknown server error occurred"
                } catch (parseException: Exception) {
                    "Failed to parse server error"
                }
            }

            is IOException -> {
                "Network issue detected. Please check your connection."
            }

            else -> {
                "Unexpected error. Please try again later."
            }
        }
    }
}