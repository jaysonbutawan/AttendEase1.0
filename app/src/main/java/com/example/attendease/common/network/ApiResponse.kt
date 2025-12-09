package com.example.attendease.common.network

data class ApiResponse<T>(
    val success: Boolean,
    val user: T?
)