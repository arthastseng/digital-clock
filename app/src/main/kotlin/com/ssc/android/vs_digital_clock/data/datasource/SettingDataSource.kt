package com.ssc.android.vs_digital_clock.data.datasource

import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.Error

interface SettingDataSource {
    suspend fun fetchAvailableTimeZones(): Result<List<String>, Error>
}