package com.example.attendease.common.network.request

data class UserProfileRequest(
    val firebase_uid: String,
    val firstName: String,
    val lastName: String,
    val course_id: Int
)

