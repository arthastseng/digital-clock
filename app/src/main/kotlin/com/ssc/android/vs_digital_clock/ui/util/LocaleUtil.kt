package com.ssc.android.vs_digital_clock.ui.util

import com.ssc.android.vs_digital_clock.data.datastore.SystemLanguage
import java.util.Locale

object LocaleUtil {
    fun getLocale(language: String): Locale {
        return when (language) {
            SystemLanguage.EN.code -> Locale.US
            SystemLanguage.ZH_TW.code -> Locale.TRADITIONAL_CHINESE
            else -> Locale.US
        }
    }
}