package com.ssc.android.vs_digital_clock.domain.repository

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.SystemError

interface DashboardRepository {
    suspend fun fetchTimeZone(timezone : String): Result<TimeZoneInfo, SystemError>
}