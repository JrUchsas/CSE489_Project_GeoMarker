package com.example.locmark.network

import com.example.locmark.model.Entity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api.php")
    suspend fun getEntities(): Response<List<Entity>>

    @Multipart
    @POST("api.php")
    suspend fun createEntity(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Entity>

    @FormUrlEncoded
    @PUT("api.php")
    suspend fun updateEntity(
        @Field("id") id: Int,
        @Field("title") title: String,
        @Field("lat") lat: Double,
        @Field("lon") lon: Double
    ): Response<Void>
}

