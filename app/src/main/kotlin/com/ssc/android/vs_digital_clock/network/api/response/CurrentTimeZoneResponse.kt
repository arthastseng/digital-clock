package com.ssc.android.vs_digital_clock.network.api.response

import com.google.gson.annotations.SerializedName

data class CurrentTimeZoneResponse (
    @SerializedName("year")
    val year: Int = 0,
    @SerializedName("month")
    val month: Int = 0,
    @SerializedName("day")
    val day: Int = 0,
    @SerializedName("hour")
    val hour: Int = 0,
    @SerializedName("minute")
    val minute: Int = 0,
    @SerializedName("seconds")
    val seconds: Int = 0,
    @SerializedName("milliSeconds")
    val milliSeconds: Int = 0,
    @SerializedName("dateTime")
    val dateTime: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("timeZone")
    val timeZone: String = "",
    @SerializedName("dayOfWeek")
    val dayOfWeek: String = "",
    @SerializedName("dstActive")
    val dstActive: Boolean = false,
)