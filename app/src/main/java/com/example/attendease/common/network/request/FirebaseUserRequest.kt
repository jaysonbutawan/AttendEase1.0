package com.example.attendease.common.network.request

data class FirebaseUserRequest(
    val uid: String,
    val email: String?,
    val role: String,
    val firstname: String? = null,
    val lastname: String? = null
)