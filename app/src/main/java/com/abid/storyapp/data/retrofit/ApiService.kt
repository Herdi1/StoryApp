package com.abid.storyapp.data.retrofit

import com.abid.storyapp.data.response.DetailResponse
import com.abid.storyapp.data.response.LoginResponse
import com.abid.storyapp.data.response.RegisterResponse
import com.abid.storyapp.data.response.StoryResponse
import com.abid.storyapp.data.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
     suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
     ): StoryResponse

     @GET("stories/{id}")
     fun getDetail(
        @Path("id") id: String
     ):Call<DetailResponse>

     @Multipart
     @POST("stories")
     suspend fun uploadStory(
         @Part file: MultipartBody.Part,
         @Part("description") description: RequestBody,
         @Part("lat") lat: Float?,
         @Part("lon") lon: Float?
     ): UploadResponse

     @GET("stories")
     suspend fun getStoriesWithLocation(
         @Query("location") location: Int = 1,
     ): StoryResponse
}