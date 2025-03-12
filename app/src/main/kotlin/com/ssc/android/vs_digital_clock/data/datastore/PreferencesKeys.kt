package com.ssc.android.vs_digital_clock.data.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val KEY_REFRESH_RATE = intPreferencesKey("refresh_rate")
    val KEY_LANGUAGE = stringPreferencesKey("system_language")
}