package com.ssc.android.vs_digital_clock.domain.repository.impl

import com.ssc.android.vs_digital_clock.data.datasource.PreferenceDataSource
import com.ssc.android.vs_digital_clock.domain.repository.PreferenceDataRepository
import javax.inject.Inject

class PreferenceDataRepositoryImpl @Inject constructor(
    private val dataSource: PreferenceDataSource
) : PreferenceDataRepository {
    override suspend fun getRefreshRate(): Int {
        return dataSource.getRefreshRate()
    }

    override suspend fun setRefreshRate(rate: Int) {
        dataSource.setRefreshRate(rate = rate)
    }
}