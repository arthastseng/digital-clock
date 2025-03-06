package com.ssc.android.vs_digital_clock.network

import android.util.Log
import com.ssc.android.vs_digital_clock.BuildConfig
import com.ssc.android.vs_digital_clock.network.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.API_URL
    private const val TAG = "RetrofitClient"

    val retrofit: Retrofit by lazy {
        Log.d(TAG,"Base url : $BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}