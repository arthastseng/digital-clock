package com.ssc.android.vs_digital_clock.data

import java.io.Serializable

data class TimeZoneInfo(
    val time: String = "",
    val timeZone: String = ""
) : Serializable
