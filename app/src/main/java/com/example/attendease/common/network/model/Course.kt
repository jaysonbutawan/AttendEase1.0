package com.example.attendease.common.network.model

import com.google.gson.annotations.SerializedName

data class Course(
    val id: Int,
    @SerializedName("course_name") val name: String
)

