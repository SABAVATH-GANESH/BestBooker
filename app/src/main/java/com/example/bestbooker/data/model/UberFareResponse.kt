package com.example.bestbooker.data.model

data class UberFareResponse(
    val fare_id: String,
    val display: String,
    val amount: Double,
    val currency: String,
    val ride_type: String
)