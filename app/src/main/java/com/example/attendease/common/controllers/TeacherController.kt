package com.example.attendease.common.controllers

import com.example.attendease.common.network.ApiClient
import com.example.attendease.common.network.model.TeacherData
import com.example.attendease.common.network.request.TeacherProfileRequest

class TeacherController {

    private val apiService = ApiClient.instance

    suspend fun updateUserProfile(firebase_uid: String, firstName: String, lastName: String, contact_number: String): Result<Unit> {
        return try {
            val request = TeacherProfileRequest(firebase_uid,firstName, lastName,contact_number)
            val response = apiService.teacherUpdateProfile(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Laravel profile update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(firebaseUid: String): Result<TeacherData> {
        return try {

            val request = mapOf("firebase_uid" to firebaseUid)

            val response = ApiClient.instance.teacherProfile(request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}