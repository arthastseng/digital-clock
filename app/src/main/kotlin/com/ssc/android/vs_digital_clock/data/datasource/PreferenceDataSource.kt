package com.ssc.android.vs_digital_clock.data.datasource

interface PreferenceDataSource {
    suspend fun getRefreshRate(): Int
    suspend fun setRefreshRate(rate: Int)
}