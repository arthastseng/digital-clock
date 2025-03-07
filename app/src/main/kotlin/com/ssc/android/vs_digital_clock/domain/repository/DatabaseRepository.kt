package com.ssc.android.vs_digital_clock.domain.repository

import com.ssc.android.vs_digital_clock.data.db.TimeZone

interface DatabaseRepository {
    suspend fun getAllTimeZones(): List<TimeZone>
    suspend fun insertTimeZone(timeZone: TimeZone)
    suspend fun deleteTimeZones(timeZones: List<TimeZone>)
}