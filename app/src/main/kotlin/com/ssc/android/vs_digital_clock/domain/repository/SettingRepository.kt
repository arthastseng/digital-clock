package com.ssc.android.vs_digital_clock.domain.repository

import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.Error

interface SettingRepository {
    suspend fun fetchAvailableTimeZones(): Result<List<String>, Error>
}