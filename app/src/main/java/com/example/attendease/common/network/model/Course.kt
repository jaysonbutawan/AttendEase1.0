package com.example.attendease.common.network.model

import com.google.gson.annotations.SerializedName

data class Course(
    val course_id: Int,
    @SerializedName("course_name") val name: String
)

