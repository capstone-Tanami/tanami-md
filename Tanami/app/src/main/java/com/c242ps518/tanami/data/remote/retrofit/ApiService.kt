package com.c242ps518.tanami.data.remote.retrofit

import com.c242ps518.tanami.data.remote.response.AddPostResponse
import com.c242ps518.tanami.data.remote.response.ArticlesResponse
import com.c242ps518.tanami.data.remote.response.DeleteHistoryResponse
import com.c242ps518.tanami.data.remote.response.FieldResponse
import com.c242ps518.tanami.data.remote.response.HistoryResponse
import com.c242ps518.tanami.data.remote.response.LoginResponse
import com.c242ps518.tanami.data.remote.response.OpenWeatherResponse
import com.c242ps518.tanami.data.remote.response.PostsResponse
import com.c242ps518.tanami.data.remote.response.ProfileResponse
import com.c242ps518.tanami.data.remote.response.RegisterResponse
import com.c242ps518.tanami.data.remote.response.UpdateProfileResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String
    ): OpenWeatherResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("identifier") identifier: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("posts")
    suspend fun getPosts(
        @Header("Authorization") token: String,
    ): PostsResponse

    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): AddPostResponse

    @Multipart
    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part?,
        @Part("name") name: RequestBody?
    ): UpdateProfileResponse

    @GET("me")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): ProfileResponse

    @POST("predict/fields")
    suspend fun predictField(
        @Header("Authorization") token: String,
        @Body request: FieldRequest
    ): FieldResponse

    @Multipart
    @POST("predict/image")
    suspend fun predictImage(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part?,
        @Part("N") n: RequestBody,
        @Part("P") p: RequestBody,
        @Part("K") k: RequestBody,
        @Part("temperature") temperature: RequestBody,
        @Part("humidity") humidity: RequestBody,
        @Part("ph") ph: RequestBody,
        @Part("rainfall") rainfall: RequestBody
    ): FieldResponse

    @GET("predictions")
    suspend fun getHistory(
        @Header("Authorization") token: String
    ): HistoryResponse

    @GET("predictions")
    suspend fun get5History(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 5
    ): HistoryResponse

    @GET("articles")
    suspend fun getArticles(): ArticlesResponse

    @DELETE("prediction/{PredictionID}")
    suspend fun deletePrediction(
        @Header("Authorization") token: String,
        @Path("PredictionID") predictionId: Int
    ): DeleteHistoryResponse
}

data class FieldRequest(
    @SerializedName("inputData") val inputData: InputData
)

data class InputData(
    @SerializedName("N") val n: Float,
    @SerializedName("P") val p: Float,
    @SerializedName("K") val k: Float,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("humidity") val humidity: Float,
    @SerializedName("ph") val ph: Float,
    @SerializedName("rainfall") val rainfall: Float
)
