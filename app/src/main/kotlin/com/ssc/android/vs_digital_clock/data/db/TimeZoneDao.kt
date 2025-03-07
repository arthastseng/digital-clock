package com.ssc.android.vs_digital_clock.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TimeZoneDao {
    @Query("SELECT * FROM timezone")
    suspend fun getAllTimeZone(): List<TimeZone>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTimeZone(timeZone: TimeZone)

    @Delete
    suspend fun deleteTimeZones(timeZones: List<TimeZone>)
}