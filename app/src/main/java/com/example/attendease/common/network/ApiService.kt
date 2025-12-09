package com.example.attendease.common.network

import com.example.attendease.common.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/google-auth")
    suspend fun googleLogin(
        @Body data: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/firebase-register")
    suspend fun registerFirebaseUser(
        @Body request: FirebaseUserRequest
    ): ApiResponse<FirebaseUserResponse>
}
