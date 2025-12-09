package com.example.attendease.common.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatar: String?,
    val googleId: String?
)