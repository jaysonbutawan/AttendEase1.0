package com.example.attendease.common.network

data class FirebaseUserResponse(
    val id: Int?,
    val firebase_uid: String?,
    val email: String?,
    val role: String?
)
