package com.example.bestbooker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val provider: String, // Uber/Ola/Rapido
    val pickupLat: Double,
    val pickupLng: Double,
    val dropLat: Double,
    val dropLng: Double,
    val fare: Double,
    val createdAt: Long = System.currentTimeMillis()
)
