package com.ssc.android.vs_digital_clock.data.datasource.impl

import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.data.datasource.SettingDataSource
import com.ssc.android.vs_digital_clock.network.RetrofitClient
import com.ssc.android.vs_digital_clock.network.api.base.Error
import javax.inject.Inject

class SettingDataSourceImpl @Inject constructor() : SettingDataSource {

    private val apiService = RetrofitClient.apiService

    override suspend fun fetchAvailableTimeZones(): Result<List<String>, Error> {
        return try {
            val result = apiService.getAvailableTimeZones()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(Error(errorMsg = e.message.toString()))
        }
    }
}