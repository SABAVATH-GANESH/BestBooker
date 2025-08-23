package com.example.bestbooker.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object NetworkModule {
    private const val BASE_URL = "https://api.uber.com/"

    fun provideUberApi(): UberApi {
        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(UberApi::class.java)
    }
}