package com.example.attendease.common.network.model

data class StudentData(
    val student_id: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val contact_number: String?,
    val course_id: Int?,
    val course_name: String?,
    val year: Int?,
    val status: String?,
    val created_at: String
)