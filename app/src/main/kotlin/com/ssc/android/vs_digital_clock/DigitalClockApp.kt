package com.ssc.android.vs_digital_clock

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import androidx.datastore.preferences.preferencesDataStore

@HiltAndroidApp
class DigitalClockApp : Application() {
    companion object {
        val Context.dataStore by preferencesDataStore(name = "app_preferences")
    }
}