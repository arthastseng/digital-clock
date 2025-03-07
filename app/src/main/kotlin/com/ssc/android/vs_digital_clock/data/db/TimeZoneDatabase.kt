package com.ssc.android.vs_digital_clock.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeZone::class], version = 1, exportSchema = false)
abstract class TimeZoneDatabase : RoomDatabase() {
    abstract fun timeZoneDao(): TimeZoneDao

    companion object {
        @Volatile
        private var instance: TimeZoneDatabase? = null
        private const val TIME_ZONE_DATABASE_NAME = "time_zone_database"

        fun getInstance(context: Context): TimeZoneDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): TimeZoneDatabase {
            return Room.databaseBuilder(
                context,
                TimeZoneDatabase::class.java,
                TIME_ZONE_DATABASE_NAME
            ).build()
        }
    }
}