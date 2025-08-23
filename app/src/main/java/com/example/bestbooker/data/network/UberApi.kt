package com.example.bestbooker.data.network

import com.example.bestbooker.data.model.UberFareResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UberApi {
    @GET("v1.2/estimates/price")
    suspend fun getFareEstimate(
        @Header("Authorization") authHeader: String,
        @Query("start_latitude") startLat: Double,
        @Query("start_longitude") startLng: Double,
        @Query("end_latitude") endLat: Double,
        @Query("end_longitude") endLng: Double
    ): Response<List<UberFareResponse>>
}