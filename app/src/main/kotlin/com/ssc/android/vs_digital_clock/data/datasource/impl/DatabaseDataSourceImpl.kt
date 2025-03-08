package com.ssc.android.vs_digital_clock.data.datasource.impl

import com.ssc.android.vs_digital_clock.data.datasource.DatabaseDataSource
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.data.db.TimeZoneDao
import javax.inject.Inject

class DatabaseDataSourceImpl @Inject constructor(
    private val timeZoneDao: TimeZoneDao
) : DatabaseDataSource {
    override suspend fun getAllTimeZones(): List<TimeZone> {
        return timeZoneDao.getAllTimeZone()
    }

    override suspend fun insertTimeZone(timeZone: TimeZone) {
        timeZoneDao.insertTimeZone(timeZone = timeZone)
    }

    override suspend fun deleteTimeZones(timeZones: List<TimeZone>) {
        timeZoneDao.deleteTimeZones(timeZones = timeZones)
    }
}