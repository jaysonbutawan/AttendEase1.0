package com.example.attendease.common.network

import com.example.attendease.common.network.reponse.ApiResponse
import com.example.attendease.common.network.reponse.FirebaseUserResponse
import com.example.attendease.common.network.model.StudentData
import com.example.attendease.common.network.reponse.CourseResponse
import com.example.attendease.common.network.request.FirebaseUserRequest
import com.example.attendease.common.network.request.UserProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

//student profile update and get profile
    @POST("auth/update-profile")
    suspend fun updateProfile(
        @Body request: UserProfileRequest
    ): Response<ApiResponse<StudentData>>

    @POST("auth/get-profile")
    suspend fun getProfile(
        @Body request: Map<String, String>
    ): Response<ApiResponse<StudentData>>

    @POST("auth/firebase-register")
    suspend fun registerFirebaseUser(
        @Body request: FirebaseUserRequest
    ): ApiResponse<FirebaseUserResponse>

    @GET("auth/courses")
    suspend fun getCourses(): Response<CourseResponse>

}
