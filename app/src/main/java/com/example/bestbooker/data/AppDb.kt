package com.example.bestbooker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Booking::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        fun get(ctx: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(ctx, AppDb::class.java, "bestbooker.db").build().also { INSTANCE = it }
        }
    }
}
