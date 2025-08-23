package com.example.bestbooker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingDao {
    @Insert suspend fun insert(b: Booking)
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    suspend fun all(): List<Booking>
}
