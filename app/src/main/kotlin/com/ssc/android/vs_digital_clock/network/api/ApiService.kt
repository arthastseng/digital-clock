package com.ssc.android.vs_digital_clock.network.api

import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.network.api.response.CurrentTimeZoneResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /**
     * Gets all the available IANA time zones
     */
    @GET("timezone/availabletimezones")
    suspend fun getAvailableTimeZones(): List<String>

    /**
     * Gets the current time of a time zone
     */
    @GET("time/current/zone")
    suspend fun getCurrentTimeZone(@Query("timeZone") timezone: String): CurrentTimeZoneResponse
}