package com.ssc.android.vs_digital_clock.data.db

import androidx.annotation.WorkerThread

class TimeZoneDatabaseRepository(private val timeZoneDao: TimeZoneDao) {
    @WorkerThread
    suspend fun getAllTimeZones() {
        timeZoneDao.getAllTimeZone()
    }

    @WorkerThread
    suspend fun insertTimeZone(timeZone: TimeZone) = timeZoneDao.insertTimeZone()

    @WorkerThread
    suspend fun deleteTimeZones(timeZones: List<TimeZone>) {
        timeZoneDao.deleteTimeZones(timeZones = timeZones)
    }
}