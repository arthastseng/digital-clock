package com.ssc.android.vs_digital_clock.data.datasource

import com.ssc.android.vs_digital_clock.data.db.TimeZone

interface DatabaseDataSource {
    suspend fun getAllTimeZones(): List<TimeZone>
    suspend fun insertTimeZone(timeZone: TimeZone)
    suspend fun deleteTimeZones(timeZones: List<TimeZone>)
}