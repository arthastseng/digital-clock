package com.ssc.android.vs_digital_clock.domain.repository

interface PreferenceDataRepository {
    suspend fun getRefreshRate(): Int
    suspend fun setRefreshRate(rate: Int)
    suspend fun getLanguage(): String
    suspend fun setLanguage(language: String)
}