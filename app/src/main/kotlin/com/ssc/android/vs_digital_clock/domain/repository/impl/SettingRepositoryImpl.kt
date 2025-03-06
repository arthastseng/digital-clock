package com.ssc.android.vs_digital_clock.domain.repository.impl

import com.ssc.android.vs_digital_clock.data.datasource.SettingDataSource
import com.ssc.android.vs_digital_clock.domain.repository.SettingRepository
import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val dataSource: SettingDataSource
) : SettingRepository {
    override suspend fun fetchAvailableTimeZones(): Result<List<String>, SystemError> {
        return dataSource.fetchAvailableTimeZones()
    }
}