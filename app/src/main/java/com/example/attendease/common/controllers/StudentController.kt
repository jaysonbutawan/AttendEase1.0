package com.example.attendease.common.controllers

import com.example.attendease.common.network.ApiClient
import com.example.attendease.common.network.model.StudentData
import com.example.attendease.common.network.request.UserProfileRequest

class StudentController {

    private val apiService = ApiClient.instance

    suspend fun updateUserProfile(firebase_uid: String, firstName: String, lastName: String, course_id: Int): Result<Unit> {
        return try {
            val request = UserProfileRequest(firebase_uid,firstName, lastName,course_id)
            val response = apiService.updateProfile(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Laravel profile update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(firebaseUid: String): Result<StudentData> {
        return try {

            val request = mapOf("firebase_uid" to firebaseUid)

            val response = ApiClient.instance.getProfile(request)

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