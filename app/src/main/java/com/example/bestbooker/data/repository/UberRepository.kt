package com.example.bestbooker.data.repository

import com.example.bestbooker.data.model.UberFareResponse
import com.example.bestbooker.data.network.UberApi

class UberRepository(private val api: UberApi) {
    suspend fun fetchFareEstimate(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        token: String
    ): List<UberFareResponse>? {
        val response = api.getFareEstimate(
            authHeader = "Bearer $token",
            startLat = startLat,
            startLng = startLng,
            endLat = endLat,
            endLng = endLng
        )
        return if (response.isSuccessful) response.body() else null
    }
}