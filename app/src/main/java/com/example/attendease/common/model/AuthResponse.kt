package com.example.attendease.common.model

data class AuthResponse(
    val token: String,
    val user: User
)