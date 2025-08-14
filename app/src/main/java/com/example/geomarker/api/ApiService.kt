package com.example.geomarker.api

import com.example.geomarker.model.Entity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

interface ApiService {

    @POST("api.php")
    @Multipart
    suspend fun createEntity(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("api.php")
    suspend fun getEntities(): Response<List<Entity>>

    @PUT("api.php")
    @FormUrlEncoded
    suspend fun updateEntity(
        @Field("id") id: Int,
        @Field("title") title: String,
        @Field("lat") lat: Double,
        @Field("lon") lon: Double,
        @Field("image") image: String? // Assuming image is sent as a URL or base64 string for update
    ): Response<ResponseBody>

    @DELETE("api.php")
    suspend fun deleteEntity(@Query("id") id: Int): Response<ResponseBody>
}

object RetrofitClient {
    private const val BASE_URL = "https://labs.anontech.info/cse489/t3/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}