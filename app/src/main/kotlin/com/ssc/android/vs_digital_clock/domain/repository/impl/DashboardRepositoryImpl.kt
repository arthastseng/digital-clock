package com.ssc.android.vs_digital_clock.domain.repository.impl

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.data.datasource.DashboardDataSource
import com.ssc.android.vs_digital_clock.domain.repository.DashboardRepository
import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dataSource: DashboardDataSource
) : DashboardRepository {
    override suspend fun fetchTimeZone(timezone: String): Result<TimeZoneInfo, SystemError> {
        return dataSource.fetchTimeZone(timezone = timezone)
    }
}