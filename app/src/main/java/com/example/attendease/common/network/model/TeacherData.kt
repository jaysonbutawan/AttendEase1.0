package com.example.attendease.common.network.model

import com.google.gson.annotations.SerializedName

data class TeacherData(
    val teacher_id: String,
    val firebase_uid: String,
    val email: String,
    val contact_number: String,
    val firstname: String,
    val lastname: String,
    val status: String?,
    val created_at: String,



)
