package com.example.attendease.common.network

object ApiClient {
    private const val BASE_URL = "http://192.168.1.14:8000/api/"

    val instance: ApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

