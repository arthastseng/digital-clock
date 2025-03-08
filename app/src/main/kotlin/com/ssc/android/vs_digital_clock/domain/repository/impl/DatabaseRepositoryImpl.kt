package com.ssc.android.vs_digital_clock.domain.repository.impl

import com.ssc.android.vs_digital_clock.data.datasource.DatabaseDataSource
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.domain.repository.DatabaseRepository
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val dataSource: DatabaseDataSource
) : DatabaseRepository {
    override suspend fun getAllTimeZones(): List<TimeZone> {
        return dataSource.getAllTimeZones()
    }

    override suspend fun insertTimeZone(timeZone: TimeZone) {
        dataSource.insertTimeZone(timeZone = timeZone)
    }

    override suspend fun deleteTimeZones(timeZones: List<TimeZone>) {
        dataSource.deleteTimeZones(timeZones = timeZones)
    }

}