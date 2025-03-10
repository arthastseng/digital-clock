package com.ssc.android.vs_digital_clock.data.datasource.impl

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.data.datasource.DashboardDataSource
import com.ssc.android.vs_digital_clock.network.RetrofitClient
import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import javax.inject.Inject

class DashboardDataSourceImpl @Inject constructor() : DashboardDataSource {
    private val apiService = RetrofitClient.apiService
    override suspend fun fetchTimeZone(timezone: String): Result<TimeZoneInfo, SystemError> {
        return try {
            val result = apiService.getCurrentTimeZone(timezone)
            val data = TimeZoneInfo(
                time = result.time,
                timeZone = result.timeZone
            )
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(SystemError(errorMsg = e.message.toString()))
        }
    }
}