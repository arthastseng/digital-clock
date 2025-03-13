package com.ssc.android.vs_digital_clock.data.datasource.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.ssc.android.vs_digital_clock.DigitalClockApp.Companion.dataStore
import com.ssc.android.vs_digital_clock.data.datasource.PreferenceDataSource
import com.ssc.android.vs_digital_clock.data.datastore.PreferencesKeys
import com.ssc.android.vs_digital_clock.data.datastore.RefreshRate
import com.ssc.android.vs_digital_clock.data.datastore.SystemLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class PreferenceDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceDataSource {
    private val dataStore = context.dataStore

    override suspend fun getRefreshRate(): Int {
        val preference = dataStore.data.first()
        return preference[PreferencesKeys.KEY_REFRESH_RATE] ?: RefreshRate.ONE_MINUTE.rate
    }

    override suspend fun setRefreshRate(rate: Int) {
        runBlocking {
            dataStore.edit { preference ->
                preference[PreferencesKeys.KEY_REFRESH_RATE] = rate
            }
        }
    }

    override suspend fun getLanguage(): String {
        val preference = dataStore.data.first()
        return preference[PreferencesKeys.KEY_LANGUAGE] ?: SystemLanguage.EN.code
    }

    override suspend fun setLanguage(language: String) {
        runBlocking {
            dataStore.edit { preference ->
                preference[PreferencesKeys.KEY_LANGUAGE] = language
            }
        }
    }
}