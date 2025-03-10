package com.ssc.android.vs_digital_clock.data.datasource.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.ssc.android.vs_digital_clock.DigitalClockApp.Companion.dataStore
import com.ssc.android.vs_digital_clock.data.datasource.PreferenceDataSource
import com.ssc.android.vs_digital_clock.data.datastore.PreferencesKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class PreferenceDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceDataSource {
    private val dataStore = context.dataStore
    override suspend fun getRefreshRate(): Int {
        val dataFlow = dataStore.data
        return runBlocking {
            val preference = dataFlow.first()
            preference[PreferencesKeys.KEY_REFRESH_RATE] ?: 1
        }
    }

    override suspend fun setRefreshRate(rate: Int) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.KEY_REFRESH_RATE] = rate
        }
    }
}