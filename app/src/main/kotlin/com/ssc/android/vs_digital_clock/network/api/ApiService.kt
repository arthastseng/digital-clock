package com.ssc.android.vs_digital_clock.network.api

import retrofit2.http.GET

interface ApiService {
    @GET("timezone/availabletimezones")
    suspend fun getAvailableTimeZones(): List<String>
}